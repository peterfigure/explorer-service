package io.provenance.explorer.domain.entities

import com.google.protobuf.Any
import com.google.protobuf.Timestamp
import cosmos.tx.v1beta1.ServiceOuterClass
import io.provenance.explorer.OBJECT_MAPPER
import io.provenance.explorer.domain.core.sql.jsonb
import io.provenance.explorer.domain.extensions.toDateTime
import io.provenance.explorer.domain.models.explorer.GasStatistics
import io.provenance.explorer.domain.models.explorer.TxStatus
import io.provenance.explorer.domain.models.explorer.getCategoryForType
import io.provenance.explorer.grpc.extensions.getAssociatedAddresses
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Avg
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.Max
import org.jetbrains.exposed.sql.Min
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import org.jetbrains.exposed.sql.jodatime.CustomDateTimeFunction
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

object TxCacheTable : CacheIdTable<String>(name = "tx_cache") {
    val hash = varchar("hash", 64)
    override val id = hash.entityId()
    val height = reference("height", BlockCacheTable.height)
    val gasWanted = integer("gas_wanted")
    val gasUsed = integer("gas_used")
    val txTimestamp = datetime("tx_timestamp")
    val errorCode = integer("error_code").nullable()
    val codespace = varchar("codespace", 16).nullable()
    val txV2 = jsonb<TxCacheTable, ServiceOuterClass.GetTxResponse>("tx_v2", OBJECT_MAPPER)
}

class TxCacheRecord(id: EntityID<String>) : CacheEntity<String>(id) {
    companion object : CacheEntityClass<String, TxCacheRecord>(TxCacheTable) {
        fun insertIgnore(tx: ServiceOuterClass.GetTxResponse, txTime: Timestamp) =
            transaction {
                TxCacheTable.insertIgnoreAndGetId {
                    it[hash] = tx.txResponse.txhash
                    it[height] = tx.txResponse.height.toInt()
                    if (tx.txResponse.code > 0) it[errorCode] = tx.txResponse.code
                    if (tx.txResponse.codespace.isNotBlank()) it[codespace] = tx.txResponse.codespace
                    it[gasUsed] = tx.txResponse.gasUsed.toInt()
                    it[gasWanted] = tx.txResponse.gasWanted.toInt()
                    it[txTimestamp] = txTime.toDateTime()
                    it[txV2] = tx
                    it[hitCount] = 0
                    it[lastHit] = DateTime.now()
                }.let {
                    tx.tx.body.messagesList.forEachIndexed { idx, msg ->
                        if (tx.txResponse.logsCount > 0)
                            tx.txResponse.logsList[0].eventsList
                                .filter { event -> event.type == "message" }[idx]
                                .let { event ->
                                    val type = event.attributesList.first { att -> att.key == "action" }.value
                                    val module = event.attributesList.first { att -> att.key == "module" }.value
                                    TxMessageRecord.insert(tx.txResponse.height.toInt(), it!!, msg, type, module)
                                }
                        else
                            TxMessageRecord.insert(tx.txResponse.height.toInt(), it!!, msg, "unknown", "unknown")

                        TxAddressJoinRecord.insert(it!!, tx.txResponse.height.toInt(), msg.getAssociatedAddresses())
                    }
                }
                tx.tx.authInfo.signerInfosList.forEach { sig ->
                    SignatureJoinRecord.insert(
                        sig.publicKey,
                        SigJoinType.TRANSACTION,
                        tx.txResponse.txhash
                    )
                }
            }

        fun findByHeight(height: Int) =
            TxCacheRecord.find { TxCacheTable.height eq height }

        fun findSigsByHash(hash: String) = SignatureRecord.findByJoin(SigJoinType.TRANSACTION, hash)

        fun getGasStats(startDate: DateTime, endDate: DateTime, granularity: String) = transaction {
            val dateTrunc = CustomDateTimeFunction("DATE_TRUNC", stringLiteral(granularity),  TxCacheTable.txTimestamp)
            val minGas = Min(TxCacheTable.gasUsed, IntegerColumnType())
            val maxGas = Max(TxCacheTable.gasUsed, IntegerColumnType())
            val avgGas = Avg(TxCacheTable.gasUsed, 5)

            TxCacheTable.slice(dateTrunc, minGas, maxGas, avgGas)
                .select { TxCacheTable.txTimestamp.between(startDate, endDate.plusDays(1)) }
                .groupBy(dateTrunc)
                .orderBy(dateTrunc, SortOrder.DESC)
                .map { GasStatistics(
                    it[dateTrunc]!!.withZone(DateTimeZone.UTC).toString("yyyy-MM-dd HH:mm:ss"),
                    it[minGas]!!,
                    it[maxGas]!!,
                    it[avgGas]!!
                ) }
        }
    }

    var hash by TxCacheTable.hash
    var height by TxCacheTable.height
    var gasWanted by TxCacheTable.gasWanted
    var gasUsed by TxCacheTable.gasUsed
    var txTimestamp by TxCacheTable.txTimestamp
    var errorCode by TxCacheTable.errorCode
    var codespace by TxCacheTable.codespace
    var txV2 by TxCacheTable.txV2
    override var lastHit by TxCacheTable.lastHit
    override var hitCount by TxCacheTable.hitCount
    val txMessages by TxMessageRecord referrersOn TxMessageTable.txHash
}

object TxMessageTypeTable : IntIdTable(name = "tx_message_type") {
    val type = varchar("type", 128)
    val module = varchar("module", 128)
    val protoType = varchar("proto_type", 256)
    val category = varchar("category", 128).nullable()
}

class TxMessageTypeRecord(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TxMessageTypeRecord>(TxMessageTypeTable) {

        private fun findByProtoType(protoType: String) = transaction {
            TxMessageTypeRecord.find { TxMessageTypeTable.protoType eq protoType }.firstOrNull()
        }

        fun findByType(types: List<String>) = transaction {
            TxMessageTypeRecord.find { TxMessageTypeTable.type inList types }
        }

        fun insert(type: String, module: String, protoType: String) = transaction {
            findByProtoType(protoType)?.let {
                if (it.type == "unknown" && type != "unknown") {
                    it.apply {
                        this.type = type
                        this.module = module
                    }.id
                } else it.id
            } ?: TxMessageTypeTable.insertAndGetId {
                it[this.type] = type
                it[this.module] = module
                it[this.protoType] = protoType
                if (type.getCategoryForType() != null)
                    it[this.category] = type.getCategoryForType()!!.mainCategory
            }
        }

    }

    var type by TxMessageTypeTable.type
    var module by TxMessageTypeTable.module
    var protoType by TxMessageTypeTable.protoType
    var category by TxMessageTypeTable.category
}

object TxMessageTable : IntIdTable(name = "tx_message") {
    val blockHeight = integer("block_height")
    val txHash = reference("tx_hash", TxCacheTable)
    val txMessageType = reference("tx_message_type_id", TxMessageTypeTable)
    val txMessage = jsonb<TxMessageTable, Any>("tx_message", OBJECT_MAPPER)
}

class TxMessageRecord(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TxMessageRecord>(TxMessageTable) {

        fun findByHash(hash: String) = transaction {
            TxMessageRecord.find { TxMessageTable.txHash eq hash }
        }

        fun insert(blockHeight: Int, txHash: EntityID<String>, message: Any, type: String, module: String) =
            transaction {
            TxMessageTypeRecord.insert(type, module, message.typeUrl).let { typeId ->
                TxMessageTable.insert {
                    it[this.blockHeight] = blockHeight
                    it[this.txHash] = txHash
                    it[this.txMessageType] = typeId
                    it[this.txMessage] = message
                }
            }
        }

        fun findByQueryParams(
            address: String?,
            msgTypes: List<String>,
            txHeight: Int?,
            txStatus: TxStatus?,
            count: Int,
            offset: Int,
            fromDate: DateTime?,
            toDate: DateTime?
        ) = transaction {
            val query =
                TxMessageTable
                    .innerJoin(TxAddressJoinTable, { TxMessageTable.txHash }, { TxAddressJoinTable.txHash })
                    .innerJoin(TxCacheTable, { TxMessageTable.txHash }, { TxCacheTable.hash })
                    .innerJoin(TxMessageTypeTable, { TxMessageTable.txMessageType }, { TxMessageTypeTable.id })
                    .selectAll()

            if (msgTypes.isNotEmpty())
                query.andWhere { TxMessageTypeTable.type inList msgTypes }
            if (txHeight != null)
                query.andWhere { TxCacheTable.height eq txHeight }
            if (txStatus != null)
                query.andWhere {
                    if (txStatus == TxStatus.FAILURE) TxCacheTable.errorCode neq 0 else TxCacheTable.errorCode eq null }
            if (address != null)
                query.andWhere { TxAddressJoinTable.address eq address }
            if (fromDate != null)
                query.andWhere { TxCacheTable.txTimestamp greaterEq fromDate }
            if (toDate != null)
                query.andWhere { TxCacheTable.txTimestamp lessEq toDate.plusDays(1) }

            query.orderBy(Pair(TxMessageTable.blockHeight, SortOrder.DESC))
            val totalCount = query.count()
            query.limit(count, offset.toLong())
            TxCacheRecord.wrapRows(query).toList() to totalCount
        }
    }

    var blockHeight by TxMessageTable.blockHeight
    var txHash by TxCacheRecord referencedOn TxMessageTable.txHash
    var txMessageType by TxMessageTypeRecord referencedOn TxMessageTable.txMessageType
    var txMessage by TxMessageTable.txMessage
}

object TxAddressJoinTable : IntIdTable(name = "tx_address_join") {
    val blockHeight = integer("block_height")
    val txHash = reference("tx_hash", TxCacheTable)
    val address = varchar("address", 128)
}

class TxAddressJoinRecord(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TxAddressJoinRecord>(TxAddressJoinTable) {

        private fun findByHashAndAddress(txHash: EntityID<String>, address: String) = transaction {
            TxAddressJoinRecord
                .find { (TxAddressJoinTable.txHash eq txHash) and (TxAddressJoinTable.address eq address) }
                .firstOrNull()
        }

        fun findValidatorsByTxHash(txHash: EntityID<String>) = transaction {
            StakingValidatorCacheRecord.wrapRows(
                TxAddressJoinTable
                    .innerJoin(
                        StakingValidatorCacheTable,
                        { TxAddressJoinTable.address },
                        { StakingValidatorCacheTable.operatorAddress })
                    .select { (TxAddressJoinTable.txHash eq txHash) }
            ).toList()
        }

        fun insert(txHash: EntityID<String>, blockHeight: Int, addresses: List<String>) = transaction {
            addresses.forEach { addr ->
                findByHashAndAddress(txHash, addr) ?: TxAddressJoinTable.insert {
                    it[this.blockHeight] = blockHeight
                    it[this.txHash] = txHash
                    it[this.address] = addr
                }
            }
        }

    }

    var blockHeight by TxAddressJoinTable.blockHeight
    var txHash by TxCacheRecord referencedOn TxAddressJoinTable.txHash
    var address by TxAddressJoinTable.address
}

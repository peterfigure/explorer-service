package io.provenance.explorer.domain.entities

import cosmos.base.tendermint.v1beta1.Query
import cosmos.staking.v1beta1.Staking
import io.provenance.explorer.OBJECT_MAPPER
import io.provenance.explorer.domain.core.logger
import io.provenance.explorer.domain.core.sql.jsonb
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object ValidatorAddressesTable : IntIdTable(name = "validator_addresses") {
    val consensusPubkey = varchar("consensus_pubkey", 96).uniqueIndex()
    val accountAddress = varchar("account_address", 96).uniqueIndex()
    val operatorAddress = varchar("operator_address", 96).uniqueIndex()
    val consensusAddress = varchar("consensus_address", 96).uniqueIndex()
}

class ValidatorAddressesRecord(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ValidatorAddressesRecord>(ValidatorAddressesTable) {
        fun findByAccount(address: String) = transaction {
            ValidatorAddressesRecord.find { ValidatorAddressesTable.accountAddress eq address }.firstOrNull()
        }

        fun findByConsensusPubkey(pubkey: String) = transaction {
            ValidatorAddressesRecord.find { ValidatorAddressesTable.consensusPubkey eq pubkey }.firstOrNull()
        }

        fun findByConsensusAddress(address: String) = transaction {
            ValidatorAddressesRecord.find { ValidatorAddressesTable.consensusAddress eq address }.firstOrNull()
        }

        fun findByOperator(address: String) = transaction {
            ValidatorAddressesRecord.find { ValidatorAddressesTable.operatorAddress eq address }.firstOrNull()
        }

        fun insertIgnore(account: String, operator: String, consensusPubkey: String, consensusAddress : String) =
            transaction {
            ValidatorAddressesTable.insertIgnore {
                it[this.consensusPubkey] = consensusPubkey
                it[this.accountAddress] = account
                it[this.operatorAddress] = operator
                it[this.consensusAddress] = consensusAddress
            }
        }
    }

    var consensusPubkey by ValidatorAddressesTable.consensusPubkey
    var accountAddress by ValidatorAddressesTable.accountAddress
    var operatorAddress by ValidatorAddressesTable.operatorAddress
    var consensusAddress by ValidatorAddressesTable.consensusAddress
}


object ValidatorsCacheTable : CacheIdTable<Int>(name = "validators_cache") {
    val height = integer("height")
    override val id = height.entityId()
    val validators = jsonb<ValidatorsCacheTable, Query.GetValidatorSetByHeightResponse>("validators", OBJECT_MAPPER)
}

class ValidatorsCacheRecord(id: EntityID<Int>) : CacheEntity<Int>(id) {
    companion object : CacheEntityClass<Int, ValidatorsCacheRecord>(ValidatorsCacheTable) {
        fun insertIgnore(blockHeight: Int, json: Query.GetValidatorSetByHeightResponse) =
            transaction {
                ValidatorsCacheTable.insertIgnore {
                    it[this.height] = blockHeight
                    it[this.validators] = json
                    it[this.hitCount] = 0
                    it[this.lastHit] = DateTime.now()
                }.let { json }
            }
    }

    var height by ValidatorsCacheTable.height
    var validators by ValidatorsCacheTable.validators
    override var lastHit by ValidatorsCacheTable.lastHit
    override var hitCount by ValidatorsCacheTable.hitCount
}


object StakingValidatorCacheTable : CacheIdTable<String>(name = "staking_validator_cache") {
    val operatorAddress = varchar("operator_address", 96)
    override val id = operatorAddress.entityId()
    val stakingValidator = jsonb<StakingValidatorCacheTable, Staking.Validator>("staking_validator", OBJECT_MAPPER)
    val moniker = varchar("moniker", 128)
    val status = varchar("status", 64)
    val jailed = bool("jailed")
}

class StakingValidatorCacheRecord(id: EntityID<String>) : CacheEntity<String>(id) {
    companion object : CacheEntityClass<String, StakingValidatorCacheRecord>(StakingValidatorCacheTable) {

        val logger = logger(StakingValidatorCacheRecord::class)

        fun insertIgnore(operatorAddress: String, json: Staking.Validator) =
            transaction {
                StakingValidatorCacheTable.insertIgnore {
                    it[this.operatorAddress] = operatorAddress
                    it[this.stakingValidator] = json
                    it[this.moniker] = json.description.moniker
                    it[this.status] = json.status.name
                    it[this.jailed] = json.jailed
                    it[this.hitCount] = 0
                    it[this.lastHit] = DateTime.now()
                }.let { json }
            }

        fun findByStatus(status: String) = transaction {
            when (status) {
                "active" -> StakingValidatorCacheRecord.find {
                    StakingValidatorCacheTable.status eq Staking.BondStatus.BOND_STATUS_BONDED.name }
                "jailed" -> StakingValidatorCacheRecord.find { StakingValidatorCacheTable.jailed eq true }
                "candidate" -> StakingValidatorCacheRecord.find {
                    (StakingValidatorCacheTable.status neq Staking.BondStatus.BOND_STATUS_BONDED.name) and
                        (StakingValidatorCacheTable.jailed eq false) }
                else -> listOf<StakingValidatorCacheRecord>()
                    .also { logger.error("This status is not supported: $status") }
            }
        }
    }

    var operatorAddress by StakingValidatorCacheTable.operatorAddress
    var stakingValidator by StakingValidatorCacheTable.stakingValidator
    var moniker by StakingValidatorCacheTable.moniker
    var status by StakingValidatorCacheTable.status
    var jailed by StakingValidatorCacheTable.jailed
    override var lastHit by StakingValidatorCacheTable.lastHit
    override var hitCount by StakingValidatorCacheTable.hitCount
}

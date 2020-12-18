package io.provenance.explorer.domain

import com.fasterxml.jackson.databind.JsonNode

data class PbTxSearchResponse(val totalCount: String, val count: String, val pageNumber: String, val limit: String, val txs: List<PbTransaction>)

data class PbTransaction(val height: String, val txhash: String, val codespace: String?, val code: Int?, val rawLog: String, val logs: List<TxLog>?, val gasWanted: String, val gasUsed: String, val tx: Tx, val timestamp: String)

data class Tx(val type: String, val value: TxValue)

data class TxValue(val msg: List<TxMsg>, val fee: TxFee, val signatures: List<TxSignature>, val memo: String)

data class TxMsg(val type: String, val value: JsonNode)

data class TxFee(val gas: String, val amount: List<DenomAmount>)

data class DenomAmount(val denom: String, val amount: String)

data class TxSignature(val pubKey: PubKey, val signature: String)

data class PbValidatorsResponse(val blockHeight: String, val validators: List<PbValidator>)

data class PbValidator(val address: String, val pubKey: String, val proposerPriority: String, val votingPower: String)

data class PbResponse<T>(val height: String, val result: T)

data class PbStakingValidator(val operatorAddress: String, val consensusPubkey: String, val jailed: Boolean, val status: Int, val tokens: String, val delegatorShares: String, val description: ValidatorDescription,
                              val bondHeight: String?, val bondIntraTxCounter: String?, val unbondingHeight: String, val unbondingTime: String, val commission: Commission, val minSelfDelegation: String)

data class ValidatorDescription(val moniker: String, val identity: String, val website: String, val securityContact: String, val details: String)

data class ValidatorDistribution(val operatorAddress: String, val selfBondRewards: DenomAmount, val valCommission: DenomAmount)

data class Commission(val commissionRates: CommissionRates, val updateTime: String)

data class CommissionRates(val rate: String, val maxRate: String, val maxChangeRate: String)

data class SigningInfo(val address: String, val startHeight: String, val indexOffset: String, val jailedUntil: String, val tombstoned: Boolean, val missedBlocksCounter: String)
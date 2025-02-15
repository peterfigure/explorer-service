package io.provenance.explorer.domain.models.explorer

data class Contract(
    val contractAddress: String,
    val creationHeight: Int,
    val codeId: Int,
    val creator: String,
    val admin: String?,
    val label: String?
)

data class Code(
    val codeId: Int,
    val creationHeight: Int,
    val creator: String?,
    val dataHash: String?
)

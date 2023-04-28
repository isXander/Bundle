package dev.isxander.bundle.source.modrinth.request

import kotlinx.serialization.Serializable

@Serializable
data class BulkGetVersionsFromHashesRequest(
    val hashes: List<String>,
    val algorithm: HashAlgorithm,
)

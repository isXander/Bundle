package dev.isxander.bundle.source.modrinth.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BulkGetLatestVersionsFromHashesRequest(
    val hashes: List<String>,
    val algorithm: HashAlgorithm,
    val loaders: List<String>,
    @SerialName("game_versions") val gameVersions: List<String>,
) {
    @Serializable
    enum class HashAlgorithm {
        @SerialName("sha1") SHA1,
        @SerialName("sha512") SHA512,
    }
}



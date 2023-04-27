package dev.isxander.bundle.source.modrinth.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModFile(
    val hashes: Hashes,
    val url: String,
    val filename: String,
    val size: Long,
    @SerialName("file_type") val fileType: String?,
    val primary: Boolean,
) {
    @Serializable
    data class Hashes(
        val sha1: String,
        val sha512: String,
    )
}
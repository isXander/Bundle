package dev.isxander.bundle.source.modrinth.response

import dev.isxander.bundle.utils.downloadFile
import dev.isxander.bundle.utils.httpClient
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
    fun asModFile(): dev.isxander.bundle.mod.ModFile {
        return dev.isxander.bundle.mod.ModFile(
            filename,
            hashes.sha512,
            size
        ) {
            httpClient.downloadFile(url, it)
        }
    }

    @Serializable
    data class Hashes(
        val sha1: String,
        val sha512: String,
    )
}
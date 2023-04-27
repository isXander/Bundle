package dev.isxander.bundle.source.modrinth.response

import dev.isxander.bundle.ModMeta
import dev.isxander.bundle.source.RemoteModMeta
import dev.isxander.bundle.utils.downloadFile
import dev.isxander.bundle.utils.httpClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.quiltmc.loader.api.Version

typealias ModVersionResponse = List<ModVersionData>

@Serializable
data class ModVersionData(
    @SerialName("version_number") val versionNumber: String,
    @SerialName("game_versions") val gameVersions: List<String>,
    val loaders: List<String>,
    @SerialName("date_published") val datePublished: String,
    val files: List<ModFile>,
    @SerialName("version_type") val versionType: VersionType
) {
    fun asModMeta(): RemoteModMeta {
        val primaryFile = files.firstOrNull { it.primary } ?: files.first()
        return RemoteModMeta(primaryFile.filename, primaryFile.hashes.sha512) {
            httpClient.downloadFile(files.first().url) { bytesSentTotal, contentLength ->
                it((bytesSentTotal.toDouble() / contentLength).toFloat())
            }
        }
    }
}


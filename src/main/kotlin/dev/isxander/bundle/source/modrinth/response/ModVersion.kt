package dev.isxander.bundle.source.modrinth.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModVersion(
    @SerialName("version_number") val versionNumber: String,
    @SerialName("project_id") val projectId: String,
    val files: List<ModFile>,
    val changelog: String,
)


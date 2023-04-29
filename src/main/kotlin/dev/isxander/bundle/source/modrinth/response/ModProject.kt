package dev.isxander.bundle.source.modrinth.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModProject(
    val id: String,
    val title: String,
    val slug: String,
    @SerialName("icon_url") val iconUrl: String?,
)

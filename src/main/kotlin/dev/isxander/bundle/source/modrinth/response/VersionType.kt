package dev.isxander.bundle.source.modrinth.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class VersionType {
    @SerialName("release") RELEASE,
    @SerialName("beta") BETA,
    @SerialName("alpha") ALPHA,
}
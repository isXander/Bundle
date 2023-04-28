package dev.isxander.bundle.source.modrinth.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HashAlgorithm {
    @SerialName("sha1") SHA1,
    @SerialName("sha512") SHA512,
}
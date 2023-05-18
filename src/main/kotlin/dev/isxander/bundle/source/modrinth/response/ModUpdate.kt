package dev.isxander.bundle.source.modrinth.response

import dev.isxander.bundle.ctx.Version
import dev.isxander.bundle.mod.Mod
import dev.isxander.bundle.mod.ModMeta
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModUpdate(
    @SerialName("version_number") val versionNumber: String,
    @SerialName("game_versions") val gameVersions: List<String>,
    val loaders: List<String>,
    val files: List<ModFile>,
    @SerialName("version_type") val versionType: VersionType
) {
    fun asMod(localMod: Mod): Mod {
        return Mod(
            files.first().asModFile(),
            ModMeta(
                localMod.meta?.name ?: localMod.file.fileName,
                Version.of(versionNumber)
            )
        )
    }
}

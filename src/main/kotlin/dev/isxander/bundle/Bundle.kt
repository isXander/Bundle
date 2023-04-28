package dev.isxander.bundle

import dev.isxander.bundle.gui.LoadingGui
import dev.isxander.bundle.mod.Mod
import dev.isxander.bundle.mod.ModFile
import dev.isxander.bundle.source.modrinth.ModrinthModSource
import dev.isxander.bundle.utils.UpdateCandidate
import dev.isxander.bundle.utils.logger
import dev.isxander.bundle.utils.sha512
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.quiltmc.loader.api.QuiltLoader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.walk

object Bundle {
    val BUNDLE_MOD_FOLDER: Path = QuiltLoader.getGameDir().resolve("bundle-mods")

    fun startBlocking() {
        runBlocking { start() }
    }

    suspend fun start() {
        logger.info("Starting Bundle...")

        if (!Files.exists(BUNDLE_MOD_FOLDER)) {
            Files.createDirectory(BUNDLE_MOD_FOLDER)
        }

        val outdatedMods = getOutdatedMods(discoverMods())
        if (outdatedMods.isEmpty()) {
            logger.info("No outdated mods found!")
            return
        }

        updateMods(outdatedMods)
    }

    @OptIn(ExperimentalPathApi::class)
    private suspend fun discoverMods(): List<Mod> {
        logger.info("Discovering mods...")

        val localMods = mutableListOf<Mod>()
        for (mod in BUNDLE_MOD_FOLDER.walk()) {
            if (mod.extension == "disabled") continue
            if (mod.name.startsWith(".")) continue

            getMod(mod).let { localMods.add(it) }
        }

        ModrinthModSource.bulkFillMeta(localMods)

        logger.info("Mod Candidates: " + localMods.joinToString(prefix = "[", postfix = "]") { it.meta?.name ?: it.file.fileName })

        return localMods;
    }

    private suspend fun getOutdatedMods(mods: List<Mod>): List<UpdateCandidate> {
        logger.info("Getting outdated mods...")

        return ModrinthModSource.bulkGetLatest(mods)
            .filter { it.remote.file.sha512 != it.local.file.sha512 }
    }

    private fun getMod(jarPath: Path): Mod {
        return Mod(
            ModFile(
                jarPath.name,
                sha512(jarPath),
                Files.size(jarPath)
            ),
            null
        )
    }

    private suspend fun updateMods(mods: List<UpdateCandidate>) {
        logger.info("Updating Mods...")

        val loadingGui = LoadingGui()
        loadingGui.startDownload(mods.sumOf { it.remote.file.size })
        loadingGui.isVisible = true

        coroutineScope {
            mods.mapIndexed { index, (local, remote) -> async {
                val current = BUNDLE_MOD_FOLDER.resolve(local.file.fileName)
                val updated = BUNDLE_MOD_FOLDER.resolve(remote.file.fileName)

                logger.info("Downloading: ${remote.file.fileName}")
                remote.file.download { bytesSent, _ -> loadingGui.setModProgress(index, bytesSent) }
                    ?.takeIf { sha512(it) == remote.file.sha512 }
                    ?.let {
                        Files.write(updated, it, StandardOpenOption.CREATE_NEW)
                        Files.delete(current)
                        logger.info("Downloaded: ${remote.file.fileName}")
                } ?: logger.error("Failed to download: ${remote.file.fileName}")
            }}.awaitAll()
        }

        loadingGui.dispose()
    }
}
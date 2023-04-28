package dev.isxander.bundle

import com.google.common.hash.Hashing
import dev.isxander.bundle.source.modrinth.ModrinthModSource
import dev.isxander.bundle.utils.UpdateCandidate
import dev.isxander.bundle.utils.logger
import dev.isxander.bundle.utils.sha512
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.quiltmc.loader.api.QuiltLoader
import org.quiltmc.loader.impl.fabric.metadata.FabricModMetadataReader
import org.quiltmc.loader.impl.metadata.FabricLoaderModMetadata
import org.quiltmc.loader.impl.metadata.qmj.ModMetadataReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.zip.ZipFile
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
    private fun discoverMods(): List<ModMeta> {
        logger.info("Discovering mods...")

        val localMods = mutableListOf<ModMeta>()
        for (mod in BUNDLE_MOD_FOLDER.walk()) {
            if (mod.extension == "disabled") continue

            getModMeta(mod).let { localMods.add(it) }
        }

        logger.info("Mod Candidates: " + localMods.joinToString(prefix = "[", postfix = "]") { it.fileName })

        return localMods;
    }

    private suspend fun getOutdatedMods(mods: List<ModMeta>): List<UpdateCandidate> {
        logger.info("Getting outdated mods...")

        return ModrinthModSource.bulkGetLatest(mods)
            .filter { it.remote.sha512 != it.local.sha512 }
    }

    private fun getModMeta(jarPath: Path): ModMeta {
        return ModMeta(
            fileName = jarPath.name,
            sha512 = sha512(jarPath),
        )
    }

    private suspend fun updateMods(mods: List<UpdateCandidate>) {
        logger.info("Updating Mods...")

        coroutineScope {
            mods.map { (local, remote) -> async {
                val current = BUNDLE_MOD_FOLDER.resolve(local.fileName)
                val updated = BUNDLE_MOD_FOLDER.resolve(remote.fileName)

                logger.info("Downloading: ${remote.fileName}")
                remote.download()
                    ?.takeIf { sha512(it) == remote.sha512 }
                    ?.let {
                        Files.write(updated, it, StandardOpenOption.CREATE_NEW)
                        Files.delete(current)
                        logger.info("Downloaded: ${remote.fileName}")
                } ?: logger.error("Failed to download: ${remote.fileName}")
            }}.awaitAll()
        }
    }
}
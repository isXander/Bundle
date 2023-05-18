package dev.isxander.bundle

import dev.isxander.bundle.config.BundleConfig
import dev.isxander.bundle.ctx.BundleContext
import dev.isxander.bundle.gui.LoadingGui
import dev.isxander.bundle.mod.Mod
import dev.isxander.bundle.mod.ModFile
import dev.isxander.bundle.source.modrinth.ModrinthModSource
import dev.isxander.bundle.utils.UpdateCandidate
import dev.isxander.bundle.utils.logger
import dev.isxander.bundle.utils.sha512
import kotlinx.coroutines.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.*

object Bundle {
    lateinit var LOADER_CTX: BundleContext

    val BUNDLE_MOD_FOLDER: Path by lazy { LOADER_CTX.gameDir.resolve("bundle-mods") }
    val BACKUP_MOD_FOLDER: Path by lazy { LOADER_CTX.gameDir.resolve("bundle-mods-backup") }

    fun startBlocking() {
        runBlocking { start() }
    }

    suspend fun start() {
        logger.info("Starting Bundle...")

        if (!Files.exists(BUNDLE_MOD_FOLDER)) {
            Files.createDirectory(BUNDLE_MOD_FOLDER)
        }

        if (hasBackupLockFile()) {
            logger.info("Backup lock file found, skipping update and restoring original state...")
            restoreModBackup()
            return
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
            .also {
                for ((local, remote) in it) {
                    logger.info("Outdated: ${local.meta?.name ?: local.file.fileName} (${local.meta?.version ?: "unknown version"} -> ${remote.meta?.version ?: "unknown version"})")
                }
            }
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

        backupModFolder()

        val loadingGui = LoadingGui()
        loadingGui.startDownload(mods.sumOf { it.remote.file.size })
        loadingGui.isVisible = true

        coroutineScope {
            mods.mapIndexed { index, (local, remote) -> async(start = CoroutineStart.LAZY) { // w/o lazy, all will start downloading without even calling awaitAll
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
            }}
                .chunked(BundleConfig.downloadThreads) // allow 2 downloads at a time
                .forEach { it.awaitAll() }
        }

        loadingGui.dispose()
    }

    @OptIn(ExperimentalPathApi::class)
    fun onLoad(success: Boolean) {
        if (success) {
            if (hasBackupLockFile())
                BACKUP_MOD_FOLDER.deleteRecursively()
        } else {
            logger.warn("Loader failure detected. Restoring to mods before auto-update...")
            //restoreModBackup()
        }
    }

    @OptIn(ExperimentalPathApi::class)
    private fun backupModFolder() {
        logger.info("Backing up Mod Folder...")

        if (hasBackupLockFile()) error("Backup lock file exists! Cannot backup with previous failure.")
        if (BACKUP_MOD_FOLDER.exists())
            BACKUP_MOD_FOLDER.deleteRecursively()

        BUNDLE_MOD_FOLDER.copyToRecursively(
            target = BACKUP_MOD_FOLDER,
            overwrite = false,
            followLinks = true
        )

        Files.createFile(BACKUP_MOD_FOLDER.resolve(".lock"))
    }

    @OptIn(ExperimentalPathApi::class)
    private fun restoreModBackup() {
        logger.info("Restoring Mod Backup...")

        BUNDLE_MOD_FOLDER.deleteRecursively()

        // must delete before copying, as quilt loader opens all files in mod folder and does not close them
        Files.deleteIfExists(BACKUP_MOD_FOLDER.resolve(".lock"))

        try {
            BACKUP_MOD_FOLDER.copyToRecursively(
                target = BUNDLE_MOD_FOLDER,
                overwrite = false,
                followLinks = true
            )
            BACKUP_MOD_FOLDER.deleteRecursively()
        } catch (e: Exception) {
            Files.createDirectory(BACKUP_MOD_FOLDER)
            Files.createFile(BACKUP_MOD_FOLDER.resolve(".lock"))
        }
    }

    private fun hasBackupLockFile(): Boolean {
        return Files.exists(BACKUP_MOD_FOLDER.resolve(".lock"))
    }
}
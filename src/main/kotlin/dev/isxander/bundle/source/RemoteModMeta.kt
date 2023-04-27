package dev.isxander.bundle.source

import dev.isxander.bundle.ModMeta
import org.quiltmc.loader.api.Version

class RemoteModMeta(
    fileName: String,
    sha512: String,
    private val downloadFunction: suspend (progressTracker: (Float) -> Unit) -> ByteArray?,
) : ModMeta(fileName, sha512) {
    suspend fun download(progressTracker: (Float) -> Unit = {}): ByteArray? {
        return downloadFunction(progressTracker)
    }
}

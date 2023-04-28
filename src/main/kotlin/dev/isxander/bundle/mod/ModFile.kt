package dev.isxander.bundle.mod

import java.lang.UnsupportedOperationException

data class ModFile(
    val fileName: String,
    val sha512: String,
    val size: Long,
    private val downloadFunction: suspend (progressTracker: (Long, Long) -> Unit) -> ByteArray? = { throw UnsupportedOperationException() },
) {
    suspend fun download(progressTracker: (Long, Long) -> Unit = { _, _ ->}): ByteArray? {
        return downloadFunction(progressTracker)
    }
}
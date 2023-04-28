package dev.isxander.bundle.utils

import com.google.common.hash.Hashing
import com.google.common.io.Files
import java.nio.file.Path

fun sha512(path: Path): String =
    Files.asByteSource(path.toFile()).hash(Hashing.sha512()).toString()

fun sha512(bytes: ByteArray): String =
    Hashing.sha512().hashBytes(bytes).toString()
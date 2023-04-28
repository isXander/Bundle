package dev.isxander.bundle.source

import dev.isxander.bundle.mod.Mod
import dev.isxander.bundle.utils.UpdateCandidate

interface RemoteModSource {
    suspend fun bulkGetLatest(local: List<Mod>): List<UpdateCandidate>
}
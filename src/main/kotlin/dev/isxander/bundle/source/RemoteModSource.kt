package dev.isxander.bundle.source

import dev.isxander.bundle.ModMeta
import dev.isxander.bundle.utils.UpdateCandidate

interface RemoteModSource {
    suspend fun bulkGetLatest(local: List<ModMeta>): List<UpdateCandidate>
}
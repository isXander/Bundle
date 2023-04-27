package dev.isxander.bundle.utils

import dev.isxander.bundle.ModMeta
import dev.isxander.bundle.source.RemoteModMeta

data class UpdateCandidate(val local: ModMeta, val remote: RemoteModMeta)

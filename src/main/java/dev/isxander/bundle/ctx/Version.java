package dev.isxander.bundle.ctx;

import dev.isxander.bundle.Bundle;

public interface Version extends Comparable<Version> {
    String raw();

    static Version of(String raw) {
        return Bundle.LOADER_CTX.parseVersion(raw);
    }
}

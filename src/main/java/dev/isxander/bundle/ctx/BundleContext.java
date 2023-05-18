package dev.isxander.bundle.ctx;

import java.nio.file.Path;

public interface BundleContext {
    String getGameVersion();

    Path getGameDir();
    Path getConfigDir();

    Version parseVersion(String raw);
}

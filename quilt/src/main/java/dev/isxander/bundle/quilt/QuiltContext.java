package dev.isxander.bundle.quilt;

import dev.isxander.bundle.ctx.BundleContext;
import dev.isxander.bundle.ctx.Version;
import org.quiltmc.loader.api.QuiltLoader;

import java.nio.file.Path;

public class QuiltContext implements BundleContext {
    @Override
    public String getGameVersion() {
        return QuiltLoader.getRawGameVersion();
    }

    @Override
    public Path getGameDir() {
        return QuiltLoader.getGameDir();
    }

    @Override
    public Path getConfigDir() {
        return QuiltLoader.getConfigDir();
    }

    @Override
    public Version parseVersion(String raw) {
        return new QuiltVersion(org.quiltmc.loader.api.Version.of(raw));
    }
}

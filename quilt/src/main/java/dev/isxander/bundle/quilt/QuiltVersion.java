package dev.isxander.bundle.quilt;

import dev.isxander.bundle.ctx.Version;
import org.jetbrains.annotations.NotNull;

public class QuiltVersion implements Version {
    private final org.quiltmc.loader.api.Version version;

    public QuiltVersion(org.quiltmc.loader.api.Version version) {
        this.version = version;
    }

    @Override
    public String raw() {
        return version.raw();
    }

    @Override
    public int compareTo(@NotNull Version o) {
        return version.compareTo(((QuiltVersion) o).getQuiltVersion());
    }

    public org.quiltmc.loader.api.Version getQuiltVersion() {
        return version;
    }
}

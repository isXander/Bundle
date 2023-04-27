package dev.isxander.bundle.entrypoint;

import dev.isxander.bundle.Bundle;
import org.quiltmc.loader.api.LoaderValue;
import org.quiltmc.loader.api.plugin.QuiltLoaderPlugin;
import org.quiltmc.loader.api.plugin.QuiltPluginContext;

import java.util.Map;

public class BundleLoaderPlugin implements QuiltLoaderPlugin {
    @Override
    public void load(QuiltPluginContext context, Map<String, LoaderValue> previousData) {
        Bundle.INSTANCE.startBlocking();
        context.addFolderToScan(Bundle.INSTANCE.getBUNDLE_MOD_FOLDER());
    }

    @Override
    public void unload(Map<String, LoaderValue> data) {

    }
}

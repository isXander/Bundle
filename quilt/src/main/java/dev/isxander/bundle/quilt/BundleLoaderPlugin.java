package dev.isxander.bundle.quilt;

import dev.isxander.bundle.Bundle;
import org.quiltmc.loader.api.LoaderValue;
import org.quiltmc.loader.api.plugin.QuiltLoaderPlugin;
import org.quiltmc.loader.api.plugin.QuiltPluginContext;
import org.quiltmc.loader.api.plugin.solver.ModSolveResult;
import org.quiltmc.loader.api.plugin.solver.Rule;

import java.util.Collection;
import java.util.Map;

public class BundleLoaderPlugin implements QuiltLoaderPlugin {
    private boolean errored = false;

    @Override
    public void load(QuiltPluginContext context, Map<String, LoaderValue> previousData) {
        Bundle.INSTANCE.setLOADER_CTX(new QuiltContext());
        Bundle.INSTANCE.startBlocking();
        context.addFolderToScan(Bundle.INSTANCE.getBUNDLE_MOD_FOLDER());
    }

    @Override
    public void finish(ModSolveResult result) {
        Bundle.INSTANCE.onLoad(true);
    }

    @Override
    public boolean handleError(Collection<Rule> ruleChain) {
        if (!errored) {
            Bundle.INSTANCE.onLoad(false);
        }
        errored = true;
        return false;
    }

    @Override
    public void unload(Map<String, LoaderValue> data) {

    }
}

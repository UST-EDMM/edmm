package io.github.edmm.plugins.cfengine;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.Platform;
import io.github.edmm.core.transformation.TransformationContext;

public class CFEnginePlugin extends Plugin<CFEngineLifecycle> {

    public static final Platform CFENGINE = Platform.builder().id("cfengine").name("CFEngine").build();

    public CFEnginePlugin() {
        super(CFENGINE);
    }

    @Override
    public CFEngineLifecycle getLifecycle(TransformationContext context) {
        return new CFEngineLifecycle(context);
    }
}

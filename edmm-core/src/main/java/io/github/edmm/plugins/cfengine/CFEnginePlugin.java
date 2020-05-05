package io.github.edmm.plugins.cfengine;

import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class CFEnginePlugin extends TransformationPlugin<CFEngineLifecycle> {

    public static final TargetTechnology CFENGINE = TargetTechnology.builder().id("cfengine").name("CFEngine").build();

    public CFEnginePlugin() {
        super(CFENGINE);
    }

    @Override
    public CFEngineLifecycle getLifecycle(TransformationContext context) {
        return new CFEngineLifecycle(context);
    }
}

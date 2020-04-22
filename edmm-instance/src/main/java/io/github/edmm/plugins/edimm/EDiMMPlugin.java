package io.github.edmm.plugins.edimm;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;

public class EDiMMPlugin extends InstancePlugin<EDiMMPluginLifecycle> {
    public static final SourceTechnology EDiMM = SourceTechnology.builder().id("edimm").name("EDiMM").build();

    public EDiMMPlugin() {
        super(EDiMM);
    }

    @Override
    public EDiMMPluginLifecycle getLifecycle(InstanceTransformationContext context) {
        return new EDiMMPluginLifecycle(context);
    }
}

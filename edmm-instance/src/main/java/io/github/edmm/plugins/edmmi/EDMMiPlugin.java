package io.github.edmm.plugins.edmmi;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;

public class EDMMiPlugin extends InstancePlugin<EDMMiPluginLifecycle> {
    private static final SourceTechnology EDiMM = SourceTechnology.builder().id("edmmi").name("EDMMi").build();

    public EDMMiPlugin() {
        super(EDiMM);
    }

    @Override
    public EDMMiPluginLifecycle getLifecycle(InstanceTransformationContext context) {
        return new EDMMiPluginLifecycle(context);
    }
}

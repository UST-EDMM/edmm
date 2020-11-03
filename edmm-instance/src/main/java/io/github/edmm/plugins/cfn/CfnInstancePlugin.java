package io.github.edmm.plugins.cfn;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;

class CfnInstancePlugin extends InstancePlugin<CfnInstancePluginLifecycle> {
    private static final SourceTechnology CFN = SourceTechnology.builder().id("cfn").name("CFN").build();

    public CfnInstancePlugin() {
        super(CFN);
    }

    @Override
    public CfnInstancePluginLifecycle getLifecycle(InstanceTransformationContext context) {
        return new CfnInstancePluginLifecycle(context);
    }
}

package io.github.edmm.plugins.juju;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class JujuPlugin extends Plugin<JujuLifecycle> {

    public static final TargetTechnology JUJU = TargetTechnology.builder().id("juju").name("Juju").build();

    public JujuPlugin() {
        super(JUJU);
    }

    @Override
    public JujuLifecycle getLifecycle(TransformationContext context) {
        return new JujuLifecycle(context);
    }
}

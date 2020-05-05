package io.github.edmm.plugins.juju;

import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class JujuPlugin extends TransformationPlugin<JujuLifecycle> {

    public static final TargetTechnology JUJU = TargetTechnology.builder().id("juju").name("Juju").build();

    public JujuPlugin() {
        super(JUJU);
    }

    @Override
    public JujuLifecycle getLifecycle(TransformationContext context) {
        return new JujuLifecycle(context);
    }
}

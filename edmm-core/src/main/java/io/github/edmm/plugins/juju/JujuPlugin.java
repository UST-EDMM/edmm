package io.github.edmm.plugins.juju;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.Platform;
import io.github.edmm.core.transformation.TransformationContext;

public class JujuPlugin extends Plugin<JujuLifecycle> {

    public static final Platform JUJU = Platform.builder().id("juju").name("Juju").build();

    public JujuPlugin() {
        super(JUJU);
    }

    @Override
    public JujuLifecycle getLifecycle(TransformationContext context) {
        return new JujuLifecycle(context);
    }
}

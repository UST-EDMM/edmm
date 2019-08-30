package io.github.edmm.plugins.ansible;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.Platform;
import io.github.edmm.core.transformation.TransformationContext;

public class AnsiblePlugin extends Plugin<AnsibleLifecycle> {

    public static final Platform ANSIBLE = Platform.builder().id("ansible").name("Ansible").build();

    public AnsiblePlugin() {
        super(ANSIBLE);
    }

    @Override
    public AnsibleLifecycle getLifecycle(TransformationContext context) {
        return new AnsibleLifecycle(context);
    }
}

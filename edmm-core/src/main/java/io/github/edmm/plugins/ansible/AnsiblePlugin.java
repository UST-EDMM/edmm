package io.github.edmm.plugins.ansible;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class AnsiblePlugin extends Plugin<AnsibleLifecycle> {

    public static final TargetTechnology ANSIBLE = TargetTechnology.builder().id("ansible").name("Ansible").build();

    public AnsiblePlugin() {
        super(ANSIBLE);
    }

    @Override
    public AnsibleLifecycle getLifecycle(TransformationContext context) {
        return new AnsibleLifecycle(context);
    }
}

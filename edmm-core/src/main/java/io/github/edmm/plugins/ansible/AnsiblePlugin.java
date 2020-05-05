package io.github.edmm.plugins.ansible;

import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class AnsiblePlugin extends TransformationPlugin<AnsibleLifecycle> {

    public static final TargetTechnology ANSIBLE = TargetTechnology.builder().id("ansible").name("Ansible").build();

    public AnsiblePlugin() {
        super(ANSIBLE);
    }

    @Override
    public AnsibleLifecycle getLifecycle(TransformationContext context) {
        return new AnsibleLifecycle(context);
    }
}

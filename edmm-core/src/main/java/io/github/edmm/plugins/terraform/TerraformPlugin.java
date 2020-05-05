package io.github.edmm.plugins.terraform;

import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class TerraformPlugin extends TransformationPlugin<TerraformLifecycle> {

    public static final TargetTechnology TERRAFORM = TargetTechnology.builder().id("terraform").name("Terraform").build();

    public TerraformPlugin() {
        super(TERRAFORM);
    }

    @Override
    public TerraformLifecycle getLifecycle(TransformationContext context) {
        return new TerraformLifecycle(context);
    }
}

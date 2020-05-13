package io.github.edmm.plugins.terraform;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.plugins.rules.Rule;

public class TerraformPlugin extends TransformationPlugin<TerraformLifecycle> {

    public static final DeploymentTechnology TERRAFORM = DeploymentTechnology.builder().id("terraform").name("Terraform").build();

    public TerraformPlugin() {
        super(TERRAFORM);
    }

    @Override
    public TerraformLifecycle getLifecycle(TransformationContext context) {
        return new TerraformLifecycle(context);
    }

    @Override
    public List<Rule> getRules() {
        return new ArrayList<>();
    }
}

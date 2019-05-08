package io.github.ust.edmm.plugins.terraform;

import io.github.ust.edmm.core.plugin.Plugin;
import io.github.ust.edmm.core.transformation.Platform;
import io.github.ust.edmm.core.transformation.TransformationContext;
import org.springframework.stereotype.Component;

@Component
public class TerraformPlugin extends Plugin<TerraformLifecycle> {

    public static final Platform TERRAFORM = Platform.builder().id("terraform").name("Terraform").build();

    public TerraformPlugin() {
        super(TERRAFORM);
    }

    @Override
    public TerraformLifecycle getLifecycle(TransformationContext context) {
        return new TerraformLifecycle(context);
    }
}

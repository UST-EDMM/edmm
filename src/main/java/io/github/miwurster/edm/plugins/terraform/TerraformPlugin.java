package io.github.miwurster.edm.plugins.terraform;

import io.github.miwurster.edm.core.plugin.Plugin;
import io.github.miwurster.edm.core.transformation.Platform;
import io.github.miwurster.edm.core.transformation.TransformationContext;
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

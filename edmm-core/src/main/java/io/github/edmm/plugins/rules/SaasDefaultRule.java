package io.github.edmm.plugins.rules;

import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Saas;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.support.EdmmYamlBuilder;

public class SaasDefaultRule extends Rule {

    public SaasDefaultRule() {
        super("saas-default",
            "always supported replacement for a Saas node type");
    }

    @Override
    protected EdmmYamlBuilder fromTopology(EdmmYamlBuilder yamlBuilder) {
        return yamlBuilder.component(Saas.class);
    }

    @Override
    protected EdmmYamlBuilder toTopology(EdmmYamlBuilder yamlBuilder) {
        return yamlBuilder
            .component(WebApplication.class)
            .hostedOn(WebServer.class)
            .component(WebServer.class)
            .hostedOn(Compute.class)
            .component(Compute.class);
    }
}

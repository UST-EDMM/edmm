package io.github.edmm.plugins.rules;

import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.support.EdmmYamlBuilder;

public class PaasDefaultRule extends Rule {

    public PaasDefaultRule() {
        super("paas-default",
            "always supported replacement for a PaaS node type");
    }

    @Override
    protected EdmmYamlBuilder fromTopology(EdmmYamlBuilder yamlBuilder) {
        return yamlBuilder.component(Paas.class);
    }

    @Override
    protected EdmmYamlBuilder toTopology(EdmmYamlBuilder yamlBuilder) {
        return yamlBuilder
            .component(WebServer.class)
            .hostedOn(Compute.class)
            .component(Compute.class);
    }
}

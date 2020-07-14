package io.github.edmm.plugins.rules;

import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Dbaas;
import io.github.edmm.model.component.Dbms;
import io.github.edmm.model.support.EdmmYamlBuilder;

public class DbaasDefaultRule extends Rule {

    public DbaasDefaultRule() {
        super("dbaas-default",
            "always supported replacement for a DbaaS node type");
    }

    @Override
    protected EdmmYamlBuilder fromTopology(EdmmYamlBuilder yamlBuilder) {
        return yamlBuilder.component(Dbaas.class);
    }

    @Override
    protected EdmmYamlBuilder toTopology(EdmmYamlBuilder yamlBuilder) {
        return yamlBuilder
            .component(Dbms.class)
            .hostedOn(Compute.class)
            .component(Compute.class);
    }
}

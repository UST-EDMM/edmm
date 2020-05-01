package io.github.edmm.plugins.rules;

import io.github.edmm.model.component.Auth0;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.Saas;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.support.EdmmYamlBuilder;

public class CustomRule extends Rule {

    public CustomRule() {
        super("stub-rule",
            "rule that breaks test");
    }

    @Override
    protected String fromTopology() {
        return new EdmmYamlBuilder()
            .component(WebApplication.class)
            .dependsOn(Saas.class)
            .dependsOn(Auth0.class)
            .component(Saas.class)
            .component(Auth0.class)
            .build();
    }

    @Override
    protected String toTopology() {
        return new EdmmYamlBuilder()
            .component(WebServer.class)
            .hostedOn(Compute.class)
            .component(Compute.class)
            .build();
    }
}

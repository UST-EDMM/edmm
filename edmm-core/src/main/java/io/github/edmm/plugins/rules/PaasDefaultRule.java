package io.github.edmm.plugins.rules;

import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.support.EdmmYamlBuilder;

public class PaasDefaultRule extends Rule{

    public PaasDefaultRule(){
        super("paas-default",
            "always supported replacement for a PaaS node type",
            Integer.MAX_VALUE - 2);
    }

    @Override
    protected String fromTopology(){
        return new EdmmYamlBuilder()
            .component(Paas.class)
            .build();
    }

    @Override
    protected String toTopology(){
        return new EdmmYamlBuilder()
            .component(WebServer.class)
            .hostedOn(Compute.class)
            .component(Compute.class)
            .build();
    }
}

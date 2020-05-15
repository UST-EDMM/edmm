package io.github.edmm.plugins.cfn.rules;

import java.util.List;

import io.github.edmm.model.component.AwsBeanstalk;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.support.EdmmYamlBuilder;
import io.github.edmm.plugins.rules.Rule;

public class CfnPaasRule extends Rule {

    public CfnPaasRule() {
        super("cfn-paas", "not supporting every PaaS except AwsBeanstalk");
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

    @Override
    protected List<EdmmYamlBuilder> exceptTopologies(List<EdmmYamlBuilder> yamlBuilders) {
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        yamlBuilder.component(AwsBeanstalk.class);

        yamlBuilders.add(yamlBuilder);
        return yamlBuilders;
    }
}

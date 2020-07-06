package io.github.edmm.plugins.cfn.rules;

import io.github.edmm.model.component.AwsBeanstalk;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.support.EdmmYamlBuilder;
import io.github.edmm.plugins.rules.Rule;

public class CfnPaasRule extends Rule {

    public CfnPaasRule() {
        super("cfn-paas",
            "not supporting every PaaS except AwsBeanstalk",
            0,
            ReplacementReason.UNSUPPORTED
            );
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
    protected EdmmYamlBuilder[] exceptTopologies() {
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        yamlBuilder.component(AwsBeanstalk.class);

        return new EdmmYamlBuilder[] {yamlBuilder};
    }
}

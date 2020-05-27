package io.github.edmm.plugins.cfn.rules;

import io.github.edmm.model.component.AwsAurora;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Dbaas;
import io.github.edmm.model.component.Dbms;
import io.github.edmm.model.support.EdmmYamlBuilder;
import io.github.edmm.plugins.rules.Rule;

public class CfnDbaasRule extends Rule {

    public CfnDbaasRule() {
        super("cfn-dbaas",
            "not supporting every DbaaS except AwsAurora",
            0,
            ReplacementReason.UNSUPPORTED);
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

    @Override
    protected EdmmYamlBuilder[] exceptTopologies() {
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        yamlBuilder.component(AwsAurora.class);

        return new EdmmYamlBuilder[] {yamlBuilder};
    }
}

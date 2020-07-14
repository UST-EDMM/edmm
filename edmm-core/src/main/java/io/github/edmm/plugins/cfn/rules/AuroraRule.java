package io.github.edmm.plugins.cfn.rules;

import io.github.edmm.model.component.AwsAurora;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Dbms;
import io.github.edmm.model.support.EdmmYamlBuilder;
import io.github.edmm.plugins.rules.Rule;

public class AuroraRule extends Rule {
    public AuroraRule() {
        super("cfn-aurora",
            "suggest to use aws aurora instead of dbms",
            1,
            ReplacementReason.PREFERRED);
    }

    @Override
    protected EdmmYamlBuilder fromTopology(EdmmYamlBuilder yamlBuilder) {
        return yamlBuilder
            .component(Dbms.class)
            .hostedOn(Compute.class)
            .component(Compute.class);
    }

    @Override
    protected EdmmYamlBuilder toTopology(EdmmYamlBuilder yamlBuilder) {
        return yamlBuilder.component(AwsAurora.class);
    }
}

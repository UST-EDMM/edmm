package io.github.edmm.plugins.kubernetes.rules;

import io.github.edmm.model.component.AwsBeanstalk;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Tomcat;
import io.github.edmm.model.support.EdmmYamlBuilder;
import io.github.edmm.plugins.rules.Rule;

public class KubernetesBeanstalkRule extends Rule {

    public KubernetesBeanstalkRule() {
        super("kubernetes-beanstalk", "Suggest to use a Tomcat-based compute stack instead of AWS Beanstalk", 1, ReplacementReason.PREFERRED);
    }

    @Override
    protected EdmmYamlBuilder fromTopology(EdmmYamlBuilder yamlBuilder) {
        return yamlBuilder.component(AwsBeanstalk.class);
    }

    @Override
    protected EdmmYamlBuilder toTopology(EdmmYamlBuilder yamlBuilder) {
        return yamlBuilder
            .component(Tomcat.class)
            .hostedOn(Compute.class)
            .component(Compute.class);
    }
}

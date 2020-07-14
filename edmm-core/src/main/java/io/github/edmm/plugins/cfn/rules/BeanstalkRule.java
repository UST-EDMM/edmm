package io.github.edmm.plugins.cfn.rules;

import io.github.edmm.model.component.AwsBeanstalk;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.support.EdmmYamlBuilder;
import io.github.edmm.plugins.rules.Rule;

public class BeanstalkRule extends Rule {

    public BeanstalkRule() {
        super("cfn-beanstalk",
            "suggest to use aws beanstalk instead of web server",
            1,
            ReplacementReason.PREFERRED);
    }

    @Override
    protected EdmmYamlBuilder fromTopology(EdmmYamlBuilder yamlBuilder) {
        return yamlBuilder
            .component(WebServer.class)
            .hostedOn(Compute.class)
            .component(Compute.class);
    }

    @Override
    protected EdmmYamlBuilder toTopology(EdmmYamlBuilder yamlBuilder) {
        return yamlBuilder.component(AwsBeanstalk.class);
    }
}

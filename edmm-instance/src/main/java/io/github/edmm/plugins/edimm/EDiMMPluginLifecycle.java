package io.github.edmm.plugins.edimm;

import java.io.File;

import io.github.edmm.core.parser.YamlParser;
import io.github.edmm.core.parser.YamlTransformer;
import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EDiMMPluginLifecycle extends AbstractLifecycleInstancePlugin {
    private static final Logger logger = LoggerFactory.getLogger(EDiMMPluginLifecycle.class);

    private static final String directorySuffix = "/";
    private DeploymentInstance deploymentInstance = new DeploymentInstance();

    EDiMMPluginLifecycle(InstanceTransformationContext context) {
        super(context);
    }

    @Override
    public void prepare() {
    }

    @Override
    public void getModels() {
    }

    @Override
    public void transformToEDIMM() {
        YamlParser yamlParser = new YamlParser();
        this.deploymentInstance = yamlParser.parseYamlAndTransformToDeploymentInstance(context.getPath());
    }

    @Override
    public void transformToTOSCA() {
        TOSCATransformer toscaTransformer = new TOSCATransformer();
        ServiceTemplateInstance serviceTemplateInstance = toscaTransformer.transformEDiMMToServiceTemplateInstance(this.deploymentInstance);
    }

    @Override
    public void createYAML() {
        YamlTransformer yamlTransformer = new YamlTransformer();
        yamlTransformer.createYamlforEDiMM(this.deploymentInstance, new File(context.getPath()).getParent() + directorySuffix);
    }

    @Override
    public void cleanup() {
    }
}

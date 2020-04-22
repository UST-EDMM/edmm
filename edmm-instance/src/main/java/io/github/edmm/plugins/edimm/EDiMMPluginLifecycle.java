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
        logger.info("Skip preparation...");
    }

    @Override
    public void getModels() {
        logger.info("Skip getting models...");
    }

    @Override
    public void transformToEDIMM() {
        logger.info("Start parsing input YAML " + context.getPath());
        YamlParser yamlParser = new YamlParser();
        this.deploymentInstance = yamlParser.parseYamlAndTransformToDeploymentInstance(context.getPath());
        logger.info("Successfully parsed input YAML");
    }

    @Override
    public void transformToTOSCA() {
        logger.info("Start transforming to TOSCA");
        TOSCATransformer toscaTransformer = new TOSCATransformer();
        ServiceTemplateInstance transformedServiceTemplateInstance = TOSCATransformer.transformEDiMMToOpenTOSCA(this.deploymentInstance);
        logger.info("Transformed EDiMM to OpenTOSCA " + transformedServiceTemplateInstance.toString());
        logger.info("Finished transforming to OpenTOSCA...");
    }

    @Override
    public void createYAML() {
        logger.info("Start creating YAML of EDiMM...");
        YamlTransformer yamlTransformer = new YamlTransformer();
        String fileLocation = yamlTransformer.createYamlforEDiMM(this.deploymentInstance, new File(context.getPath()).getParent() + directorySuffix);
        logger.info("Finished creating YAML of EDiMM, saved to {}", fileLocation);
    }

    @Override
    public void cleanup() {

    }
}

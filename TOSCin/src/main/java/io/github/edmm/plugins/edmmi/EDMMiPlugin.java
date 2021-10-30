package io.github.edmm.plugins.edmmi;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.yaml.EDMMiYamlParser;
import io.github.edmm.exporter.OpenTOSCAConnector;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EDMMiPlugin extends AbstractLifecycleInstancePlugin<EDMMiPlugin> {

    private static final Logger logger = LoggerFactory.getLogger(EDMMiPlugin.class);

    public EDMMiPlugin(InstanceTransformationContext context) {
        super(context);
    }

    @Override
    public void prepare() {
    }

    @Override
    public void transformToTOSCA() {
        EDMMiYamlParser EDMMiYamlParser = new EDMMiYamlParser();
        DeploymentInstance deploymentInstance = EDMMiYamlParser.parseYamlAndTransformToDeploymentInstance(context.getOutputPath());
        TOSCATransformer toscaTransformer = new TOSCATransformer();
        ServiceTemplateInstance serviceTemplateInstance = toscaTransformer.transformEDiMMToServiceTemplateInstance(
            deploymentInstance);
        OpenTOSCAConnector.processServiceTemplateInstanceToOpenTOSCA(context.getSourceTechnology().getName(),
            serviceTemplateInstance,
            context.getOutputPath() + deploymentInstance.getName() + ".csar");
        logger.info("Transformed to OpenTOSCA Service Template Instance: {}", serviceTemplateInstance.getCsarId());
    }

    @Override
    public void storeTransformedTOSCA() {

    }

    @Override
    public void cleanup() {
    }
}

package io.github.edmm.plugins.edmmi;

import java.io.File;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.yaml.EDMMiYamlParser;
import io.github.edmm.core.yaml.EDMMiYamlTransformer;
import io.github.edmm.exporter.OpenTOSCAConnector;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EDMMiPlugin extends AbstractLifecycleInstancePlugin<EDMMiPlugin> {

    private static final Logger logger = LoggerFactory.getLogger(EDMMiPlugin.class);

    private static final String directorySuffix = "/";
    private DeploymentInstance deploymentInstance = new DeploymentInstance();

    public EDMMiPlugin(InstanceTransformationContext context) {
        super(context);
    }

    @Override
    public void prepare() {
    }

    @Override
    public void getModels() {
    }

    @Override
    public void transformToEDMMi() {
        EDMMiYamlParser EDMMiYamlParser = new EDMMiYamlParser();
        this.deploymentInstance = EDMMiYamlParser.parseYamlAndTransformToDeploymentInstance(context.getOutputPath());
    }

    @Override
    public void transformEdmmiToTOSCA() {
        TOSCATransformer toscaTransformer = new TOSCATransformer();
        ServiceTemplateInstance serviceTemplateInstance = toscaTransformer.transformEDiMMToServiceTemplateInstance(deploymentInstance);
        OpenTOSCAConnector.processServiceTemplateInstanceToOpenTOSCA(context.getSourceTechnology().getName(), serviceTemplateInstance, context.getOutputPath() + deploymentInstance.getName() + ".csar");
        logger.info("Transformed to OpenTOSCA Service Template Instance: {}", serviceTemplateInstance.getCsarId());
    }

    @Override
    public void transformDirectlyToTOSCA() {
        this.transformEdmmiToTOSCA();
    }

    @Override
    public void storeTransformedTOSCA() {

    }

    @Override
    public void createYAML() {
        EDMMiYamlTransformer EDMMiYamlTransformer = new EDMMiYamlTransformer();
        EDMMiYamlTransformer.createYamlforEDiMM(this.deploymentInstance, new File(context.getOutputPath()).getParent() + directorySuffix);
        logger.info("Saved YAML for EDMMi to {}", EDMMiYamlTransformer.getFileOutputLocation());
    }

    @Override
    public void cleanup() {
    }
}

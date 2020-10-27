package io.github.edmm.plugins.edmmi;

import java.io.File;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.yaml.EDMMiYamlParser;
import io.github.edmm.core.yaml.EDMMiYamlTransformer;
import io.github.edmm.exporter.WineryExporter;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;

public class EDMMiPluginLifecycle extends AbstractLifecycleInstancePlugin<EDMMiPluginLifecycle> {

    private static final String directorySuffix = "/";
    private DeploymentInstance deploymentInstance = new DeploymentInstance();

    EDMMiPluginLifecycle(InstanceTransformationContext context) {
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
        this.deploymentInstance = EDMMiYamlParser.parseYamlAndTransformToDeploymentInstance(context.getPath());
    }

    @Override
    public void transformToTOSCA() {
        TOSCATransformer toscaTransformer = new TOSCATransformer();
        ServiceTemplateInstance serviceTemplateInstance = toscaTransformer.transformEDiMMToServiceTemplateInstance(deploymentInstance);
        WineryExporter.processServiceTemplateInstanceToOpenTOSCA(context.getSourceTechnology().getName(), serviceTemplateInstance, context.getPath() + deploymentInstance.getName() + ".csar");
        System.out.println("Transformed to OpenTOSCA Service Template Instance: " + serviceTemplateInstance.getCsarId());
    }

    @Override
    public void createYAML() {
        EDMMiYamlTransformer EDMMiYamlTransformer = new EDMMiYamlTransformer();
        EDMMiYamlTransformer.createYamlforEDiMM(this.deploymentInstance, new File(context.getPath()).getParent() + directorySuffix);
        System.out.println("Saved YAML for EDMMi to " + EDMMiYamlTransformer.getFileOutputLocation());
    }

    @Override
    public void cleanup() {
    }
}

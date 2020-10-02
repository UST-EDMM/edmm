package io.github.edmm.plugins.edmmi;

import java.io.File;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.yaml.YamlParser;
import io.github.edmm.core.yaml.YamlTransformer;
import io.github.edmm.exporter.WineryExporter;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;

public class EDMMiPluginLifecycle extends AbstractLifecycleInstancePlugin {

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
        YamlParser yamlParser = new YamlParser();
        this.deploymentInstance = yamlParser.parseYamlAndTransformToDeploymentInstance(context.getPath());
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
        YamlTransformer yamlTransformer = new YamlTransformer();
        yamlTransformer.createYamlforEDiMM(this.deploymentInstance, new File(context.getPath()).getParent() + directorySuffix);
        System.out.println("Saved YAML for EDMMi to " + yamlTransformer.getFileOutputLocation());
    }

    @Override
    public void cleanup() {
    }
}

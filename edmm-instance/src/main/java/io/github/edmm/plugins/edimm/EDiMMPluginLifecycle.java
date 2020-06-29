package io.github.edmm.plugins.edimm;

import java.io.File;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.yaml.YamlParser;
import io.github.edmm.core.yaml.YamlTransformer;
import io.github.edmm.exporter.WineryExporter;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;

public class EDiMMPluginLifecycle extends AbstractLifecycleInstancePlugin {

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
        WineryExporter.exportServiceTemplateInstanceToWinery(serviceTemplateInstance);
        System.out.println("Transformed to OpenTOSCA Service Template Instance: " + serviceTemplateInstance.toString());
    }

    @Override
    public void createYAML() {
        YamlTransformer yamlTransformer = new YamlTransformer();
        yamlTransformer.createYamlforEDiMM(this.deploymentInstance, new File(context.getPath()).getParent() + directorySuffix);
        System.out.println("Saved YAML for EDiMM to " + yamlTransformer.getFileOutputLocation());
    }

    @Override
    public void cleanup() {
    }
}

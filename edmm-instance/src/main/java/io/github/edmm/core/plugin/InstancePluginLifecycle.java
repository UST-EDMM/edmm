package io.github.edmm.core.plugin;

import io.github.edmm.core.plugin.support.InstanceLifecyclePhaseAccess;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.exporter.WineryExporter;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;

public interface InstancePluginLifecycle extends InstanceLifecyclePhaseAccess {
    void prepare();

    void getModels();

    void transformToEDIMM();

    void transformToTOSCA();

    static void performTOSCATransformation(DeploymentInstance deploymentInstance) {
        TOSCATransformer toscaTransformer = new TOSCATransformer();
        ServiceTemplateInstance serviceTemplateInstance = toscaTransformer.transformEDiMMToServiceTemplateInstance(deploymentInstance);
        WineryExporter.exportServiceTemplateInstanceToWinery(serviceTemplateInstance);
        System.out.println("Transformed to OpenTOSCA Service Template Instance: " + serviceTemplateInstance.toString());
    }

    void createYAML();

    void cleanup();
}

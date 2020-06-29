package io.github.edmm.core.transformation;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.NodeTemplateInstance;
import io.github.edmm.model.opentosca.RelationshipTemplateInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;

public class TOSCATransformer {

    private final List<NodeTemplateInstance> nodeTemplateInstances = new ArrayList<>();
    private final List<RelationshipTemplateInstance> relationshipTemplateInstances = new ArrayList<>();
    private DeploymentInstance deploymentInstance;

    public ServiceTemplateInstance transformEDiMMToServiceTemplateInstance(DeploymentInstance deploymentInstance) {
        this.deploymentInstance = deploymentInstance;
        ServiceTemplateInstance serviceTemplateInstance = ServiceTemplateInstance.ofDeploymentInstance(this.deploymentInstance);
        createNodeTemplateInstances();
        createRelationshipTemplateInstances();
        serviceTemplateInstance.setNodeTemplateInstances(this.nodeTemplateInstances);
        serviceTemplateInstance.setRelationshipTemplateInstances(this.relationshipTemplateInstances);
        return serviceTemplateInstance;
    }

    private void createNodeTemplateInstances() {
        if (!this.isComponentInstancesExisting()) {
            return;
        }
        this.deploymentInstance.getComponentInstances().forEach(componentInstance -> {
            NodeTemplateInstance nodeTemplateInstance = NodeTemplateInstance.ofComponentInstance(this.deploymentInstance.getId(), this.deploymentInstance.getName(), componentInstance);
            this.nodeTemplateInstances.add(nodeTemplateInstance);
        });
    }

    private boolean isComponentInstancesExisting() {
        return this.deploymentInstance.getComponentInstances() != null && !this.deploymentInstance.getComponentInstances().isEmpty();
    }

    private void createRelationshipTemplateInstances() {
        if (isComponentInstancesExisting()) {
            this.deploymentInstance.getComponentInstances().forEach(componentInstance -> {
                if (componentInstance.getRelationInstances() != null && !componentInstance.getRelationInstances().isEmpty()) {
                    componentInstance.getRelationInstances().forEach(relationInstance -> {
                        RelationshipTemplateInstance relationshipTemplateInstance = RelationshipTemplateInstance.ofRelationInstance(this.deploymentInstance.getId(), relationInstance, componentInstance);
                        this.relationshipTemplateInstances.add(relationshipTemplateInstance);
                    });
                }
            });
        }
    }
}

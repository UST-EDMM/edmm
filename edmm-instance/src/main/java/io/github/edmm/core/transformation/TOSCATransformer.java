package io.github.edmm.core.transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.edimm.RelationInstance;
import io.github.edmm.model.opentosca.NodeTemplateInstance;
import io.github.edmm.model.opentosca.RelationshipTemplateInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class TOSCATransformer {

    private DeploymentInstance deploymentInstance;
    private final List<NodeTemplateInstance> nodeTemplateInstances = new ArrayList<>();

    public ServiceTemplateInstance transformEDiMMToServiceTemplateInstance(DeploymentInstance deploymentInstance) {
        this.deploymentInstance = deploymentInstance;
        ServiceTemplateInstance serviceTemplateInstance = ServiceTemplateInstance.ofDeploymentInstance(this.deploymentInstance);
        createNodeTemplateInstances();
        createRelationshipTemplateInstances();
        serviceTemplateInstance.setNodeTemplateInstances(this.nodeTemplateInstances);
        return serviceTemplateInstance;
    }

    private void createNodeTemplateInstances() {
        emptyIfNull(this.deploymentInstance.getComponentInstances()).forEach(componentInstance -> {
            NodeTemplateInstance nodeTemplateInstance = NodeTemplateInstance.ofComponentInstance(this.deploymentInstance.getId(), this.deploymentInstance.getName(), componentInstance);
            this.nodeTemplateInstances.add(nodeTemplateInstance);
        });
    }

    private void createRelationshipTemplateInstances() {
        emptyIfNull(this.deploymentInstance.getComponentInstances()).forEach(componentInstance -> {
            if (componentInstance.getRelationInstances() != null) {
                componentInstance.getRelationInstances().forEach(relationInstance -> {
                    RelationshipTemplateInstance relationshipTemplateInstance = RelationshipTemplateInstance.ofRelationInstance(this.deploymentInstance.getId(), relationInstance, componentInstance);
                    addRelationshipToNodeTemplateInstance(relationshipTemplateInstance, relationInstance, componentInstance);
                });
            }
        });
    }

    private void addRelationshipToNodeTemplateInstance(RelationshipTemplateInstance relationshipTemplateInstance, RelationInstance relationInstance, ComponentInstance componentInstance) {
        this.addToNodeTemplateInstanceAsOutgoing(relationshipTemplateInstance, componentInstance);
        this.addToNodeTemplateInstanceAsIngoing(relationshipTemplateInstance, relationInstance);
    }

    private void addToNodeTemplateInstanceAsOutgoing(RelationshipTemplateInstance relationshipTemplateInstance, ComponentInstance componentInstance) {
        Objects.requireNonNull(this.nodeTemplateInstances.stream()
            .filter(x -> x.getNodeTemplateInstanceId().equals(componentInstance.getId()))
            .findFirst()
            .orElse(null))
            .addToOutgoingRelationshipTemplateInstances(relationshipTemplateInstance);
    }

    private void addToNodeTemplateInstanceAsIngoing(RelationshipTemplateInstance relationshipTemplateInstance, RelationInstance relationInstance) {
        Objects.requireNonNull(this.nodeTemplateInstances.stream()
            .filter(x -> x.getNodeTemplateInstanceId().equals(relationInstance.getTargetInstanceId()))
            .findFirst()
            .orElse(null))
            .addToIngoingRelationshipTemplateInstances(relationshipTemplateInstance);
    }
}

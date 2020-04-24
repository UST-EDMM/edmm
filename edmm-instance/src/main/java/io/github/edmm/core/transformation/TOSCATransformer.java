package io.github.edmm.core.transformation;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.edimm.RelationInstance;
import io.github.edmm.model.opentosca.NodeTemplateInstance;
import io.github.edmm.model.opentosca.RelationshipTemplateInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class TOSCATransformer {

    DeploymentInstance deploymentInstance;
    ServiceTemplateInstance serviceTemplateInstance;
    List<NodeTemplateInstance> nodeTemplateInstances = new ArrayList<>();

    public ServiceTemplateInstance transformEDiMMToOpenTOSCA(DeploymentInstance deploymentInstance) {
        this.deploymentInstance = deploymentInstance;
        this.serviceTemplateInstance = ServiceTemplateInstance.ofDeploymentInstance(this.deploymentInstance);
        createNodeTemplateInstances();
        createRelationshipTemplateInstances();
        this.serviceTemplateInstance.setNodeTemplateInstances(this.nodeTemplateInstances);
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
        this.checkForOutgoingNodeTemplateInstance(relationshipTemplateInstance, componentInstance);
        this.checkForIngoingNodeTemplateInstance(relationshipTemplateInstance, relationInstance);
    }

    private void checkForOutgoingNodeTemplateInstance(RelationshipTemplateInstance relationshipTemplateInstance, ComponentInstance componentInstance) {
        this.nodeTemplateInstances.stream().filter(x -> x.getNodeTemplateInstanceId().equals(componentInstance.getId())).findFirst().get().addToOutgoingRelationshipTemplateInstances(relationshipTemplateInstance);
    }

    private void checkForIngoingNodeTemplateInstance(RelationshipTemplateInstance relationshipTemplateInstance, RelationInstance relationInstance) {
        this.nodeTemplateInstances.stream().filter(x -> x.getNodeTemplateInstanceId().equals(relationInstance.getTargetInstanceId())).findFirst().get().addToIngoingRelationshipTemplateInstances(relationshipTemplateInstance);
    }
}

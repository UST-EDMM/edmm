package io.github.edmm.model.opentosca;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.InstanceProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Setter
@Getter
@ToString
public class NodeTemplateInstance {
    String nodeTemplateInstanceId;
    String serviceTemplateInstanceId;
    QName nodeTemplateId;
    QName nodeType;
    TOSCAState.NodeTemplateInstanceState state;
    QName serviceTemplateId;
    List<TOSCAProperty> instanceProperties;
    private List<RelationshipTemplateInstance> outgoingRelationshipTemplateInstances;
    private List<RelationshipTemplateInstance> ingoingRelationshipTemplateInstances;

    private void createOutgoingRelationshipTemplateInstances() {
        this.outgoingRelationshipTemplateInstances = new ArrayList<>();
    }

    private void createIngoingRelationshipTemplateInstances() {
        this.ingoingRelationshipTemplateInstances = new ArrayList<>();
    }

    public void addToOutgoingRelationshipTemplateInstances(RelationshipTemplateInstance relationshipTemplateInstance) {
        if (this.outgoingRelationshipTemplateInstances == null) {
            this.createOutgoingRelationshipTemplateInstances();
        }
        this.outgoingRelationshipTemplateInstances.add(relationshipTemplateInstance);
    }

    public void addToIngoingRelationshipTemplateInstances(RelationshipTemplateInstance relationshipTemplateInstance) {
        if (this.ingoingRelationshipTemplateInstances == null) {
            this.createIngoingRelationshipTemplateInstances();
        }
        this.ingoingRelationshipTemplateInstances.add(relationshipTemplateInstance);
    }

    public static NodeTemplateInstance ofComponentInstance(String deploymentInstanceId, String deploymentInstanceName, ComponentInstance componentInstance) {
        NodeTemplateInstance nodeTemplateInstance = new NodeTemplateInstance();

        nodeTemplateInstance.setNodeTemplateInstanceId(componentInstance.getId());
        nodeTemplateInstance.setNodeType(new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TYPE_NAMESPACE, componentInstance.getType()));
        nodeTemplateInstance.setNodeTemplateId(new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TEMPL_NAMESPACE, componentInstance.getName()));
        nodeTemplateInstance.setServiceTemplateInstanceId(deploymentInstanceId);
        nodeTemplateInstance.setServiceTemplateId(new QName(OpenTOSCANamespaces.OPENTOSCA_SERVICE_TEMPL_NAMESPACE, deploymentInstanceName));
        nodeTemplateInstance.setState(componentInstance.getState().toTOSCANodeTemplateInstanceState());
        nodeTemplateInstance.setInstanceProperties(emptyIfNull(componentInstance.getInstanceProperties())
            .stream().map(InstanceProperty::convertToTOSCAProperty).collect(Collectors.toList()));

        return nodeTemplateInstance;
    }
}

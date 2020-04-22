package io.github.edmm.model.opentosca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    List<RelationshipTemplateInstance> outgoingRelationshipTemplateInstances;
    List<RelationshipTemplateInstance> ingoingRelationshipTemplateInstances;

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
}

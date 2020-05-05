package io.github.edmm.model.opentosca;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.edimm.RelationInstance;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Getter
@Setter
@ToString
public class RelationshipTemplateInstance {
    String serviceTemplateInstanceId;
    QName relationshipTemplateId;
    QName relationshipType;
    TOSCAState.RelationshipTemplateInstanceState state;
    String sourceNodeTemplateInstanceId;
    String targetNodeTemplateInstanceId;
    List<TOSCAProperty> instanceProperties;

    public static RelationshipTemplateInstance ofRelationInstance(String deploymentInstanceId, RelationInstance relationInstance, ComponentInstance componentInstance) {
        RelationshipTemplateInstance relationshipTemplateInstance = new RelationshipTemplateInstance();
        relationshipTemplateInstance.setServiceTemplateInstanceId(deploymentInstanceId);
        relationshipTemplateInstance.setRelationshipType(new QName(OpenTOSCANamespaces.OPENTOSCA_REL_TYPE_NAMESPACE, String.valueOf(relationInstance.getType())));
        relationshipTemplateInstance.setSourceNodeTemplateInstanceId(componentInstance.getId());
        relationshipTemplateInstance.setTargetNodeTemplateInstanceId(relationInstance.getTargetInstanceId());
        relationshipTemplateInstance.setRelationshipTemplateId(new QName(OpenTOSCANamespaces.OPENTOSCA_REL_TEMPL_NAMESPACE, relationInstance.getId()));
        relationshipTemplateInstance.setState(componentInstance.getState().toTOSCANodeTemplateInstanceState().convertToRelationshipTemplateInstanceState());
        relationshipTemplateInstance.setInstanceProperties(emptyIfNull(relationInstance.getInstanceProperties())
            .stream().map(InstanceProperty::convertToTOSCAProperty).collect(Collectors.toList()));

        return relationshipTemplateInstance;
    }
}

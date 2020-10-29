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
    private String serviceTemplateInstanceId;
    private String id;
    private QName relationshipType;
    private TOSCAState.RelationshipTemplateInstanceState state;
    private String sourceNodeTemplateInstanceId;
    private String targetNodeTemplateInstanceId;
    private List<TOSCAProperty> instanceProperties;

    public static RelationshipTemplateInstance ofRelationInstance(String deploymentInstanceId, RelationInstance relationInstance, ComponentInstance componentInstance) {
        RelationshipTemplateInstance relationshipTemplateInstance = new RelationshipTemplateInstance();
        relationshipTemplateInstance.setServiceTemplateInstanceId(deploymentInstanceId);
        relationshipTemplateInstance.setRelationshipType(new QName(OpenTOSCANamespaces.OPENTOSCA_BASE_TYPES, String.valueOf(relationInstance.getType().toToscaRelationBaseType())));
        // TODO think about this one, maybe use id and map with id -> name or sth like that
        relationshipTemplateInstance.setSourceNodeTemplateInstanceId(componentInstance.getId());
        relationshipTemplateInstance.setTargetNodeTemplateInstanceId(relationInstance.getTargetInstance());
        relationshipTemplateInstance.setId("con_" + relationInstance.getId());
        relationshipTemplateInstance.setState(componentInstance.getState().toTOSCANodeTemplateInstanceState().convertToRelationshipTemplateInstanceState());
        relationshipTemplateInstance.setInstanceProperties(emptyIfNull(relationInstance.getInstanceProperties())
            .stream().map(InstanceProperty::convertToTOSCAProperty).collect(Collectors.toList()));

        return relationshipTemplateInstance;
    }
}

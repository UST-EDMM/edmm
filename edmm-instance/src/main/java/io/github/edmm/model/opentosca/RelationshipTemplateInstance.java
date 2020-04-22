package io.github.edmm.model.opentosca;

import java.util.List;

import javax.xml.namespace.QName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
}

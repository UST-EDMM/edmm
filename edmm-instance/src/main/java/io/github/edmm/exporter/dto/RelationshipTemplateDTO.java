package io.github.edmm.exporter.dto;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.edmm.model.opentosca.RelationshipTemplateInstance;

import lombok.Setter;

@Setter
class RelationshipTemplateDTO {
    private String id;
    private String name;
    private String type;
    private Map<String, String> sourceElement;
    private Map<String, String> targetElement;

    static RelationshipTemplateDTO ofRelationshipTemplateInstance(RelationshipTemplateInstance relationshipTemplateInstance) {
        RelationshipTemplateDTO relationshipTemplateDTO = new RelationshipTemplateDTO();
        relationshipTemplateDTO.setId(relationshipTemplateInstance.getId());
        relationshipTemplateDTO.setName(relationshipTemplateInstance.getId());
        relationshipTemplateDTO.setType(String.valueOf(relationshipTemplateInstance.getRelationshipType()));
        Map<String, String> sourceElementMap = new LinkedHashMap<>();
        sourceElementMap.put("ref", relationshipTemplateInstance.getSourceNodeTemplateInstanceId());
        relationshipTemplateDTO.setSourceElement(sourceElementMap);
        Map<String, String> targetElementMap = new LinkedHashMap<>();
        targetElementMap.put("ref", relationshipTemplateInstance.getTargetNodeTemplateInstanceId());
        relationshipTemplateDTO.setTargetElement(targetElementMap);

        return relationshipTemplateDTO;
    }
}

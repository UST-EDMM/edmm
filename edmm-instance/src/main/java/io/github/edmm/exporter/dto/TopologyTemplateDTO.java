package io.github.edmm.exporter.dto;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.opentosca.ServiceTemplateInstance;

public class TopologyTemplateDTO {
    private List<NodeTemplateDTO> nodeTemplates;
    private List<RelationshipTemplateDTO> relationshipTemplates;

    public TopologyTemplateDTO(ServiceTemplateInstance serviceTemplateInstance) {
        this.nodeTemplates = createNodeTemplateDTOs(serviceTemplateInstance);
        this.relationshipTemplates = createRelationshipTemplateDTOs(serviceTemplateInstance);

    }

    private List<NodeTemplateDTO> createNodeTemplateDTOs(ServiceTemplateInstance serviceTemplateInstance) {
        List<NodeTemplateDTO> nodeTemplateDTOs = new ArrayList<>();
        serviceTemplateInstance.getNodeTemplateInstances().forEach(nodeTemplateInstance -> nodeTemplateDTOs.add(NodeTemplateDTO.ofNodeTemplateInstance(nodeTemplateInstance)));

        return nodeTemplateDTOs;
    }

    private List<RelationshipTemplateDTO> createRelationshipTemplateDTOs(ServiceTemplateInstance serviceTemplateInstance) {
        List<RelationshipTemplateDTO> relationshipTemplateDTOs = new ArrayList<>();
        serviceTemplateInstance.getRelationshipTemplateInstances().forEach(relationshipTemplateInstance -> relationshipTemplateDTOs.add(RelationshipTemplateDTO.ofRelationshipTemplateInstance(relationshipTemplateInstance)));

        return relationshipTemplateDTOs;
    }
}

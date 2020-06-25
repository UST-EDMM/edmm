package io.github.edmm.exporter.dto;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.opentosca.ServiceTemplateInstance;

public class TopologyTemplateDTO {
    List<NodeTemplateDTO> nodeTemplates;
    List<RelationshipTemplateDTO> relationshipTemplates;

    public TopologyTemplateDTO(ServiceTemplateInstance serviceTemplateInstance) {
        List<NodeTemplateDTO> nodeTemplateDTOS = new ArrayList<>();
        serviceTemplateInstance.getNodeTemplateInstances().forEach(nodeTemplateInstance -> nodeTemplateDTOS.add(NodeTemplateDTO.ofNodeTemplateInstance(nodeTemplateInstance)));
        this.nodeTemplates = nodeTemplateDTOS;
    }
}

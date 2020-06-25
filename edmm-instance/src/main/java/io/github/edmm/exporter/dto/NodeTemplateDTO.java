package io.github.edmm.exporter.dto;

import io.github.edmm.model.opentosca.NodeTemplateInstance;

import lombok.Setter;

@Setter
class NodeTemplateDTO {
    String name;

    static NodeTemplateDTO ofNodeTemplateInstance(NodeTemplateInstance nodeTemplateInstance) {
        NodeTemplateDTO nodeTemplateDTO = new NodeTemplateDTO();
        nodeTemplateDTO.setName(nodeTemplateInstance.getNodeTemplateId().getLocalPart());

        return nodeTemplateDTO;
    }
}

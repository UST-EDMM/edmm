package io.github.edmm.exporter.dto;

import javax.xml.namespace.QName;

import io.github.edmm.model.opentosca.NodeTemplateInstance;

import lombok.Setter;

@Setter
class NodeTemplateDTO {
    String id;
    String name;
    String type;
    // List<TOSCAProperty> properties;

    static NodeTemplateDTO ofNodeTemplateInstance(NodeTemplateInstance nodeTemplateInstance) {
        NodeTemplateDTO nodeTemplateDTO = new NodeTemplateDTO();
        nodeTemplateDTO.setId(nodeTemplateInstance.getNodeTemplateId().getLocalPart());
        nodeTemplateDTO.setName(nodeTemplateInstance.getNodeTemplateId().getLocalPart());
        nodeTemplateDTO.setType(String.valueOf(nodeTemplateInstance.getNodeType()));
        // nodeTemplateDTO.setProperties(nodeTemplateInstance.getInstanceProperties());

        return nodeTemplateDTO;
    }
}

package io.github.edmm.exporter.dto;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.edmm.model.opentosca.NodeTemplateInstance;

import lombok.Setter;

@Setter
class NodeTemplateDTO {
    private String id;
    private String name;
    private String type;
    private Map<String, Map<String, String>> properties;

    static NodeTemplateDTO ofNodeTemplateInstance(NodeTemplateInstance nodeTemplateInstance) {
        NodeTemplateDTO nodeTemplateDTO = new NodeTemplateDTO();
        nodeTemplateDTO.setId(nodeTemplateInstance.getNodeTemplateId().getLocalPart());
        nodeTemplateDTO.setName(nodeTemplateInstance.getNodeTemplateId().getLocalPart());
        nodeTemplateDTO.setType(String.valueOf(nodeTemplateInstance.getNodeType()));
        Map<String, Map<String, String>> propertyMap = new LinkedHashMap<>();
        propertyMap.put("kvproperties", new LinkedHashMap<>());
        nodeTemplateInstance.getInstanceProperties().forEach(instanceProp -> propertyMap.get("kvproperties").put(instanceProp.getName(), String.valueOf(instanceProp.getValue())));
        nodeTemplateDTO.setProperties(propertyMap);

        return nodeTemplateDTO;
    }
}

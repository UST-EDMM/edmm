package io.github.edmm.model.edimm;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.github.edmm.core.yaml.support.YamlConstants;
import io.github.edmm.core.yaml.support.YamlSupport;
import io.github.edmm.model.Artifact;
import io.github.edmm.model.Metadata;
import io.github.edmm.util.CastUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ComponentInstance extends BasicInstance {
    private String name;
    private String createdAt;
    private InstanceState.InstanceStateForComponentInstance state;
    private ComponentType type;
    private List<Artifact> artifacts;
    private List<RelationInstance> relationInstances;

    public static ComponentInstance ofYamlContent(Map<String, Object> yamlContent) {
        ComponentInstance componentInstance = new ComponentInstance();
        componentInstance.setName(yamlContent.get(YamlConstants.NAME) != null ? String.valueOf(yamlContent.get(YamlConstants.NAME)) : null);
        componentInstance.setType(yamlContent.get(YamlConstants.TYPE) != null ? ComponentType.valueOf(yamlContent.get(YamlConstants.TYPE).toString()) : null);
        componentInstance.setState(yamlContent.get(YamlConstants.STATE) != null ? InstanceState.InstanceStateForComponentInstance.valueOf(String.valueOf(yamlContent.get(YamlConstants.STATE))) : null);
        componentInstance.setId(yamlContent.get(YamlConstants.ID) != null ? String.valueOf(yamlContent.get(YamlConstants.ID)) : null);
        componentInstance.setCreatedAt(yamlContent.get(YamlConstants.CREATED_AT) != null ? String.valueOf(yamlContent.get(YamlConstants.CREATED_AT)) : null);
        componentInstance.setInstanceProperties(yamlContent.get(YamlConstants.INSTANCE_PROPERTIES) != null ? YamlSupport.getInstancePropertiesFromYamlContent(yamlContent) : Collections.emptyList());
        componentInstance.setDescription(yamlContent.get(YamlConstants.DESCRIPTION) != null ? String.valueOf(yamlContent.get(YamlConstants.DESCRIPTION)) : null);
        componentInstance.setMetadata(yamlContent.get(YamlConstants.METADATA) != null ? Metadata.of(CastUtil.safelyCastToStringObjectMap(yamlContent.get(YamlConstants.METADATA))) : Metadata.of(Collections.emptyMap()));
        componentInstance.setRelationInstances(yamlContent.get(YamlConstants.RELATION_INSTANCES) != null ? YamlSupport.getRelationInstancesFromYamlContent(yamlContent) : Collections.emptyList());

        return componentInstance;
    }
}

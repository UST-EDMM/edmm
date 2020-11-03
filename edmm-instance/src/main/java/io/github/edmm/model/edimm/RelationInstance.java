package io.github.edmm.model.edimm;

import java.util.Collections;
import java.util.Map;

import io.github.edmm.core.yaml.support.YamlConstants;
import io.github.edmm.core.yaml.support.YamlSupport;
import io.github.edmm.model.Metadata;
import io.github.edmm.util.CastUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RelationInstance extends BasicInstance {
    private RelationType type;
    private String targetInstance;

    public static RelationInstance ofYamlContent(Map<String, Object> yamlContent) {
        RelationInstance relationInstance = new RelationInstance();

        relationInstance.setId(yamlContent.get(YamlConstants.ID) != null ? String.valueOf(yamlContent.get(YamlConstants.ID)) : null);
        relationInstance.setTargetInstance(yamlContent.get(YamlConstants.RELATION_TARGET_INSTANCE) != null ? String.valueOf(yamlContent.get(YamlConstants.RELATION_TARGET_INSTANCE)) : null);
        relationInstance.setType(yamlContent.get(YamlConstants.TYPE) != null ? RelationType.valueOf(String.valueOf(yamlContent.get(YamlConstants.TYPE))) : null);
        relationInstance.setDescription(yamlContent.get(YamlConstants.DESCRIPTION) != null ? String.valueOf(yamlContent.get(YamlConstants.DESCRIPTION)) : null);
        relationInstance.setInstanceProperties(yamlContent.get(YamlConstants.INSTANCE_PROPERTIES) != null ? YamlSupport.getInstancePropertiesFromYamlContent(CastUtil.safelyCastToStringObjectMap(yamlContent.get(YamlConstants.INSTANCE_PROPERTIES))) : Collections.emptyList());
        relationInstance.setMetadata(yamlContent.get(YamlConstants.METADATA) != null ? Metadata.of(CastUtil.safelyCastToStringObjectMap(yamlContent.get(YamlConstants.METADATA))) : Metadata.of(Collections.emptyMap()));

        return relationInstance;
    }
}

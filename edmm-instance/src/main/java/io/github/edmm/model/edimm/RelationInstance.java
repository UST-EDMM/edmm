package io.github.edmm.model.edimm;

import java.util.List;
import java.util.Map;

import io.github.edmm.core.parser.support.YamlConstants;
import io.github.edmm.core.parser.support.YamlSupport;
import io.github.edmm.model.Metadata;
import io.github.edmm.model.Operation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static org.apache.commons.collections4.MapUtils.emptyIfNull;

@Getter
@Setter
@ToString
public class RelationInstance extends BasicInstance {
    private String type;
    private String targetInstanceId;
    private List<Operation> operations;

    public static RelationInstance ofYamlContent(Map<String, Object> yamlContent) {
        RelationInstance relationInstance = new RelationInstance();

        relationInstance.setId(yamlContent.get(YamlConstants.ID) != null ? String.valueOf(yamlContent.get(YamlConstants.ID)) : null);
        relationInstance.setTargetInstanceId(yamlContent.get(YamlConstants.RELATION_TARGET_INSTANCE_ID) != null ? String.valueOf(yamlContent.get(YamlConstants.RELATION_TARGET_INSTANCE_ID)) : null);
        relationInstance.setType(yamlContent.get(YamlConstants.TYPE) != null ? String.valueOf(yamlContent.get(YamlConstants.TYPE)) : null);
        relationInstance.setDescription(yamlContent.get(YamlConstants.DESCRIPTION) != null ? String.valueOf(yamlContent.get(YamlConstants.DESCRIPTION)) : null);
        relationInstance.setInstanceProperties(yamlContent.get(YamlConstants.INSTANCE_PROPERTIES) != null ? YamlSupport.getInstancePropertiesFromYamlContent(emptyIfNull((Map<String, Object>) yamlContent.get(YamlConstants.INSTANCE_PROPERTIES))) : null);
        relationInstance.setMetadata(yamlContent.get(YamlConstants.METADATA) != null ? Metadata.of(emptyIfNull((Map<String, Object>) yamlContent.get(YamlConstants.METADATA))) : null);

        return relationInstance;
    }
}

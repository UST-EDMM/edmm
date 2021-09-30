package io.github.edmm.core.plugin;

import io.github.edmm.model.edimm.ComponentType;

public interface EDMMTypeMapper {
    ComponentType toComponentType(String type);

    String extractTopLevelType(String type);

    String extractSpecificType(String type);
}

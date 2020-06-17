package io.github.edmm.core.plugin;

import io.github.edmm.model.edimm.ComponentType;

public interface TypeMapper {
    ComponentType toComponentType(String type);

    String extractTopLevelType(String type);

    String extractSpecificType(String type);
}

package io.github.edmm.plugins.heat.util;

import io.github.edmm.core.plugin.TypeMapper;
import io.github.edmm.model.edimm.ComponentType;
import io.github.edmm.plugins.heat.model.types.NovaType;

public class TypeMapperImplementation implements TypeMapper {
    private static final String NOVA_PREFIX = "OS::Nova::";

    @Override
    public ComponentType toComponentType(String type) {
        if (type.contains(NOVA_PREFIX)) {
            return this.handleTypes(type.replace(NOVA_PREFIX, ""));
        }
        // TODO make this better (handle all types, cinder, ...)
        return null;
    }

    @Override
    public ComponentType handleTypes(String type) {
        return NovaType.valueOf(type).toComponentType();
    }
}

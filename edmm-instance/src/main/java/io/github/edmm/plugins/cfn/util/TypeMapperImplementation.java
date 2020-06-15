package io.github.edmm.plugins.cfn.util;

import io.github.edmm.core.plugin.TypeMapper;
import io.github.edmm.model.edimm.ComponentType;
import io.github.edmm.plugins.cfn.model.types.EC2Type;

class TypeMapperImplementation implements TypeMapper {
    private static final String EC2_PREFIX = "AWS::EC2::";

    @Override
    public ComponentType toComponentType(String type) {
        if (type.contains(EC2_PREFIX)) {
            return this.handleTypes(type.replace(EC2_PREFIX, ""));
        }
        // TODO make this better (handle all types, cinder, etc ...)
        return null;
    }

    @Override
    public ComponentType handleTypes(String type) {
        return EC2Type.valueOf(type).toComponentType();
    }
}

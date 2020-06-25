package io.github.edmm.core.transformation;

import java.util.List;

import javax.xml.namespace.QName;

import io.github.edmm.core.plugin.TOSCATypeMapper;
import io.github.edmm.model.edimm.ComponentType;
import io.github.edmm.model.edimm.InstanceProperty;

public class TOSCATypeMapperImplementation implements TOSCATypeMapper {
    @Override
    public QName toTOSCAType(ComponentType type, List<InstanceProperty> instanceProperties) {
        return null;
    }
}

package io.github.edmm.core.plugin;

import java.util.List;

import javax.xml.namespace.QName;

import io.github.edmm.model.edimm.ComponentType;
import io.github.edmm.model.edimm.InstanceProperty;

public interface TOSCATypeMapper {
    QName toTOSCAType(ComponentType type, List<InstanceProperty> instanceProperties);
}

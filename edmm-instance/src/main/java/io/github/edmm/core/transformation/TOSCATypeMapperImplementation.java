package io.github.edmm.core.transformation;

import java.util.List;

import javax.xml.namespace.QName;

import io.github.edmm.core.plugin.TOSCATypeMapper;
import io.github.edmm.model.edimm.InstanceProperty;

public class TOSCATypeMapperImplementation implements TOSCATypeMapper {
    @Override
    public QName refineTOSCAType(QName qName, List<InstanceProperty> instanceProperties) {
        // we only have compute in puppet (for now)
        for (InstanceProperty instanceProperty : instanceProperties) {
            if (isOriginalTypeProperty(instanceProperty.getKey())) {
                // we found original type property, now try to refine based on that
                return TOSCATypeMapper.searchWineryRepositoryForType(String.valueOf(instanceProperty.getInstanceValue()));
            }
        }
        return null;
    }

    private boolean isOriginalTypeProperty(String propertyKey) {
        return propertyKey.equals("original_type");
    }
}

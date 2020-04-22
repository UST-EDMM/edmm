package io.github.edmm.model.edimm;

import io.github.edmm.model.opentosca.TOSCAProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InstanceProperty extends BasicInstance {
    String key;
    String type;
    Object instanceValue;

    public InstanceProperty(String key, String type, Object instanceValue) {
        this.key = key;
        this.type = type;
        this.instanceValue = instanceValue;
    }

    /**
     * Convert InstanceProperty of EDiMM to OpenTOSCA Property.
     *
     * @param instanceProperty: property to be converted
     * @return converted OpenTOSCA property
     */
    public static TOSCAProperty convertToTOSCAProperty(InstanceProperty instanceProperty) {
        TOSCAProperty toscaProperty = new TOSCAProperty();

        toscaProperty.setName(instanceProperty.getKey());
        toscaProperty.setValue(instanceProperty.getInstanceValue());

        return toscaProperty;
    }
}

package io.github.edmm.model.edimm;

import io.github.edmm.model.opentosca.TOSCAProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InstanceProperty extends BasicInstance {
    private String key;
    private String type;
    private Object instanceValue;

    public InstanceProperty(String key, String type, Object instanceValue) {
        this.key = key;
        this.type = type;
        this.instanceValue = instanceValue;
    }

    public static TOSCAProperty convertToTOSCAProperty(InstanceProperty instanceProperty) {
        TOSCAProperty toscaProperty = new TOSCAProperty();

        toscaProperty.setName(instanceProperty.getKey());
        toscaProperty.setValue(instanceProperty.getInstanceValue());

        return toscaProperty;
    }
}

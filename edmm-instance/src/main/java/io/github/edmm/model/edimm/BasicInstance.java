package io.github.edmm.model.edimm;

import java.util.List;

import io.github.edmm.model.Metadata;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BasicInstance {
    private String id;
    private List<InstanceProperty> instanceProperties;
    private String description;
    private Metadata metadata;
}

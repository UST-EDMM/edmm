package io.github.edmm.plugins.kubernetes.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComponentStack {
    private String name;
    private final List<ComponentContainer> components;
}

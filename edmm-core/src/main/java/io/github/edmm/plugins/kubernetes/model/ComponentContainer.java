package io.github.edmm.plugins.kubernetes.model;

import io.github.edmm.model.component.RootComponent;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComponentContainer {
    private final RootComponent component;
}

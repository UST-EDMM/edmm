package io.github.edmm.docker;

import io.github.edmm.model.Artifact;
import io.github.edmm.model.Operation;
import io.github.edmm.model.component.RootComponent;

import lombok.Value;

@Value
public class FileMapping {

    private final RootComponent component;
    private final Operation operation;
    private final Artifact artifact;
}

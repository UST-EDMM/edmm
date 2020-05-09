package io.github.edmm.core;

import java.util.Objects;
import java.util.Set;

import io.github.edmm.model.parameters.InputParameter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public final class DeploymentTechnology {

    public static final DeploymentTechnology NOOP = DeploymentTechnology.builder().id("noop").name("noop").build();

    private final String id;
    private final String name;
    private final Set<InputParameter> transformationInput;
    private final Set<InputParameter> deploymentInput;
    private final boolean deploymentSupported;

    public DeploymentTechnology(String id, String name) {
        this(id, name, null, null, false);
    }

    @JsonCreator
    public DeploymentTechnology(@JsonProperty("id") @NonNull String id, @JsonProperty("name") @NonNull String name,
                                Set<InputParameter> transformationInput, Set<InputParameter> deploymentInput,
                                boolean deploymentSupported) {
        this.id = id;
        this.name = name;
        this.transformationInput = transformationInput;
        this.deploymentInput = deploymentInput;
        this.deploymentSupported = deploymentSupported;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DeploymentTechnology deploymentTechnology = (DeploymentTechnology) object;
        return Objects.equals(id, deploymentTechnology.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

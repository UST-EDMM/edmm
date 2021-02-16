package io.github.edmm.plugins.multi.model.message;

import java.util.ArrayList;
import java.util.UUID;

import io.github.edmm.plugins.multi.model.ComponentProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeployRequest {

    private String modelId;
    private UUID correlationId;
    private ArrayList<String> components;
    private ArrayList<ComponentProperties> inputs;

    public DeployRequest() {

    }

    public DeployRequest(String modelId, UUID correlationId,
                         ArrayList<String> components,
                         ArrayList<ComponentProperties> inputs) {
        this.modelId = modelId;
        this.correlationId = correlationId;

        this.components = components;
        this.inputs = inputs;
    }

}

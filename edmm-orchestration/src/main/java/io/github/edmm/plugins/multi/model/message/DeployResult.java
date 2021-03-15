package io.github.edmm.plugins.multi.model.message;

import java.util.List;
import java.util.UUID;

import io.github.edmm.plugins.multi.model.ComponentProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeployResult {

    private String modelId;
    private UUID correlationId;
    private List<ComponentProperties> output;
}


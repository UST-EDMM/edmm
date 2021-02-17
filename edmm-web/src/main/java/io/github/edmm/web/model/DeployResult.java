package io.github.edmm.web.model;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotEmpty;

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

    @NotEmpty
    private String modelId;
    @NotEmpty
    private UUID correlationId;
    private List<ComponentProperties> output;
}

package io.github.edmm.plugins.compose.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.edmm.docker.Container;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Service {

    private String name;
    private String image;
    private String targetDirectory;
    private List<String> ports;
    private Map<String, String> envVars;
    private List<String> dependencies;

    public Service(@NonNull Container container, List<String> dependencies) {
        this.name = container.getLabel();
        this.image = container.getLabel() + ":latest";
        this.targetDirectory = container.getName();
        this.ports = container.getPorts().stream()
            .map(port -> String.valueOf(port.getValue()))
            .collect(Collectors.toList());
        this.envVars = container.getEnvVars();
        this.dependencies = dependencies;
    }
}

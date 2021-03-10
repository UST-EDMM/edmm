package io.github.edmm.plugins.compose.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.edmm.core.TransformationHelper;
import io.github.edmm.docker.Container;
import io.github.edmm.model.Property;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.var;

@Data
@NoArgsConstructor
public class Service {

    private String name;
    private String image;
    private String targetDirectory;
    private List<String> ports;
    private Map<String, String> envVars;
    private List<String> dependencies;

    public Service(@NonNull Container container, List<String> dependencies, Map<String, Property> computedProperties) {
        this.name = container.getServiceName();
        this.image = container.getLabel() + ":latest";
        this.targetDirectory = container.getName();
        this.ports = container.getPorts().stream()
            .map(port -> String.valueOf(port.getPort()))
            .collect(Collectors.toList());
        this.envVars = container.getEnvVars();
        this.dependencies = dependencies;

        // Ref computed props
        for (var p : computedProperties.entrySet()) {
            if (TransformationHelper.matchesBlacklist(p.getKey())) {
                continue;
            }
            envVars.put(p.getKey().toUpperCase(), p.getValue().getValue());
        }
    }
}

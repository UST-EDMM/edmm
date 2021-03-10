package io.github.edmm.plugins.compose.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.edmm.core.TransformationHelper;
import io.github.edmm.docker.Container;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.MongoDb;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.support.TypeWrapper;

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
    private Map<String, String> ports;
    private Map<String, String> envVars;
    private List<String> dependencies;

    public Service(@NonNull Container container, List<String> dependencies, Map<String, Property> computedProperties) {
        this.name = container.getServiceName();
        this.image = container.getLabel() + ":latest";
        this.targetDirectory = container.getName();
        this.ports = container.getPorts().stream()
            .collect(Collectors.toMap(p -> String.valueOf(p.getServicePort()), p -> String.valueOf(p.getPort())));
        this.envVars = container.getEnvVars();
        this.dependencies = dependencies;

        // Ref computed props
        for (var p : computedProperties.entrySet()) {
            if (TransformationHelper.matchesBlacklist(p.getKey())) {
                continue;
            }

            Property property = p.getValue();
            String key = p.getKey();
            String value = property.getValue();

            // FIXME: We need to overwrite a property which references a MongoDB with the default port
            RootComponent componentRef = TypeWrapper.wrapComponent(p.getValue().getComponentEntity());
            if (componentRef instanceof MongoDb) {
                if (key.contains("port")) {
                    value = "27017";
                }
            }

            envVars.put(key.toUpperCase(), value);
        }
    }
}

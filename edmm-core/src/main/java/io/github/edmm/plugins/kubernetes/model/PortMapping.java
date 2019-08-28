package io.github.edmm.plugins.kubernetes.model;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import lombok.Data;

@Data
public final class PortMapping {

    private final String name;
    private final Integer value;

    public ServicePort toServicePort() {
        return new ServicePortBuilder()
                .withName(getName())
                .withPort(value)
                .build();
    }

    public ContainerPort toContainerPort() {
        return new ContainerPortBuilder()
                .withName(getName())
                .withContainerPort(value)
                .build();
    }

    public String getName() {
        return name.replace("_", "-");
    }
}

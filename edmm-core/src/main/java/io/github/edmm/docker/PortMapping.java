package io.github.edmm.docker;

import java.util.Objects;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class PortMapping {

    private final String name;
    private final Integer port;
    private final Integer servicePort;
    private final String protocol;

    public PortMapping(String name, Integer port) {
        this.name = name;
        this.port = port;
        this.servicePort = port;
        this.protocol = null;
    }

    public ServicePort toServicePort() {
        ServicePortBuilder builder = new ServicePortBuilder()
            .withName(getName())
            .withPort(servicePort)
            .withNewTargetPort(port);
        if (protocol != null) {
            builder.withProtocol(protocol);
        }
        return builder.build();
    }

    public ContainerPort toContainerPort() {
        return new ContainerPortBuilder()
            .withName(getName())
            .withContainerPort(port)
            .build();
    }

    public String getName() {
        String value = name.replace("_", "-");
        if (value.length() > 14) {
            return value.substring(0, 14);
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortMapping that = (PortMapping) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

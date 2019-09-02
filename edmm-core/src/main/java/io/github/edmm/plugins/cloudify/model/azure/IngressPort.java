package io.github.edmm.plugins.cloudify.model.azure;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class IngressPort {
    private String name;
    private String port;
}

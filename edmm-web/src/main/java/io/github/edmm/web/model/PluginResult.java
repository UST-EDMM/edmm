package io.github.edmm.web.model;

import javax.validation.constraints.NotBlank;

import io.github.edmm.core.plugin.TransformationPlugin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PluginResult {

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    public static PluginResult of(TransformationPlugin<?> plugin) {
        return PluginResult.builder()
            .id(plugin.getDeploymentTechnology().getId())
            .name(plugin.getDeploymentTechnology().getName())
            .build();
    }
}

package io.github.edmm.web.model;

import javax.validation.constraints.NotBlank;

import io.github.edmm.core.plugin.Plugin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PluginResult {

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    public static PluginResult of(Plugin<?> plugin) {
        return PluginResult.builder()
            .id(plugin.getTargetTechnology().getId())
            .name(plugin.getTargetTechnology().getName())
            .build();
    }
}

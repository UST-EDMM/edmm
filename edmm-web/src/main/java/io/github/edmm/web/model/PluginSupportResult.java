package io.github.edmm.web.model;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PluginSupportResult {

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @PositiveOrZero
    private Double supports;

    @NotNull
    private List<String> unsupportedComponents;
}

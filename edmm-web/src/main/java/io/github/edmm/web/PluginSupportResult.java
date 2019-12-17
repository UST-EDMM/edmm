package io.github.edmm.web;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class PluginSupportResult {

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @Min(0)
    @Max(1)
    @PositiveOrZero
    private Double supports;

    @NotNull
    private List<String> unsupportedComponents;
}

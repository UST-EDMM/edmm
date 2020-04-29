package io.github.edmm.model;

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
public class PluginSupportResult {

    @NotBlank
    private final String id;

    @NotBlank
    private final String name;

    @Min(0)
    @Max(1)
    @PositiveOrZero
    private final Double supports;

    @NotNull
    private final List<String> unsupportedComponents;
}

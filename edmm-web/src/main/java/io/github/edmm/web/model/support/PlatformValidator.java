package io.github.edmm.web.model.support;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.github.edmm.core.plugin.PluginService;

public class PlatformValidator implements ConstraintValidator<Platform, String> {

    private final PluginService pluginService;

    public PlatformValidator(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public void initialize(Platform constraintAnnotation) {
    }

    @Override
    public boolean isValid(String target, ConstraintValidatorContext context) {
        List<String> availableTargets = pluginService.getPlugins().stream()
                .map(p -> p.getPlatform().getId()).collect(Collectors.toList());
        return availableTargets.contains(target);
    }
}

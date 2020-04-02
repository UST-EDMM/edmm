package io.github.edmm.web.model.support;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.github.edmm.core.plugin.PluginService;

public class TargetTechnologyValidator implements ConstraintValidator<ValidTargetTechnology, String> {

    private final PluginService pluginService;

    public TargetTechnologyValidator(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public void initialize(ValidTargetTechnology constraintAnnotation) {
    }

    @Override
    public boolean isValid(String target, ConstraintValidatorContext context) {
        List<String> availableTargets = pluginService.getPlugins().stream()
            .map(p -> p.getTargetTechnology().getId()).collect(Collectors.toList());
        return availableTargets.contains(target);
    }
}

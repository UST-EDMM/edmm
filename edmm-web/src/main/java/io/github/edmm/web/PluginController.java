package io.github.edmm.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.plugin.PluginService;
import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/plugins")
@Tag(name = "plugin", description = "The plugin API")
public class PluginController {

    private final PluginService pluginService;

    public PluginController(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Operation(summary = "Checks if a plugin supports the used components in an EDMM model.")
    @PostMapping(value = "/check-model-support",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PluginSupportResult>> checkModelSupport(@RequestBody String yaml) {
        DeploymentModel model = DeploymentModel.of(yaml);
        List<Plugin> plugins = pluginService.getPlugins();
        List<PluginSupportResult> response = new ArrayList<>();
        for (Plugin plugin : plugins) {
            TransformationContext context = new TransformationContext(model, plugin.getPlatform());
            CheckModelResult checkModelResult = pluginService.checkModel(context, plugin);
            List<String> unsupportedComponents = checkModelResult.getUnsupportedComponents().stream()
                    .map(RootComponent::getName)
                    .collect(Collectors.toList());
            PluginSupportResult.PluginSupportResultBuilder psr = PluginSupportResult.builder()
                    .id(plugin.getPlatform().getId())
                    .name(plugin.getPlatform().getName())
                    .unsupportedComponents(unsupportedComponents);
            double s = 1 - (unsupportedComponents.size() / (double) model.getComponents().size());
            psr.supports(s);
            response.add(psr.build());
        }
        return ResponseEntity.ok(response);
    }
}

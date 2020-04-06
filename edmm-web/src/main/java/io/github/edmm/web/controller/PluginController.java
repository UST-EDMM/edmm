package io.github.edmm.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import io.github.edmm.core.plugin.PluginService;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.PluginSupportResult;
import io.github.edmm.web.model.PluginResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Returns a list of supported plugins.")
    public List<PluginResult> getPlugins() {
        return pluginService.getPlugins().stream()
            .map(PluginResult::of).collect(Collectors.toList());
    }

    @PostMapping(
        value = "/check-model-support",
        consumes = MediaType.TEXT_PLAIN_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Checks if a plugin supports the used components in an EDMM model.")
    public List<PluginSupportResult> checkModelSupport(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The plain EDMM YAML input")
        @RequestBody String yaml
    ) {
        DeploymentModel model = DeploymentModel.of(yaml);
        return this.pluginService.checkModelSupport(model);
    }
}

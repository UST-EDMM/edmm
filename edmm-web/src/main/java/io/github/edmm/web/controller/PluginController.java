package io.github.edmm.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.plugin.PluginService;
import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.PluginSupportResult;
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

        List<PluginSupportResult> response = this.pluginService.checkModelSupport(model);

        return ResponseEntity.ok(response);
    }
}

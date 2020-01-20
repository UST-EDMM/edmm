package io.github.edmm.web.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import javax.validation.Valid;

import io.github.edmm.core.plugin.PluginService;
import io.github.edmm.core.transformation.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationService;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.utils.Consts;
import io.github.edmm.web.model.TransformationModel;
import io.github.edmm.web.model.TransformationResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/transform")
@Tag(name = "transform", description = "The transformation API")
public class TransformationController {

    private final TransformationService transformationService;
    private final PluginService pluginService;

    public TransformationController(TransformationService transformationService, PluginService pluginService) {
        this.transformationService = transformationService;
        this.pluginService = pluginService;
    }

    @Operation(summary = "Transforms a given EDMM model to the selected target technology.")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransformationResult> transform(@Parameter(required = true) @Valid @RequestBody TransformationModel model) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(model.getInput());
            String input = new String(decodedBytes);
            DeploymentModel deploymentModel = DeploymentModel.of(input);
            Path sourceDirectory = Files.createTempDirectory(Consts.EMPTY);
            Path targetDirectory = Files.createTempDirectory(model.getTarget() + "-");
            TargetTechnology targetTechnology = pluginService.getSupportedTargetTechnologies().stream()
                    .filter(p -> p.getId().equals(model.getTarget()))
                    .findFirst().orElseThrow(IllegalStateException::new);
            TransformationContext context = new TransformationContext(deploymentModel, targetTechnology, sourceDirectory.toFile(), targetDirectory.toFile());
            transformationService.startTransformation(context);
            return ResponseEntity.ok(TransformationResult.of(context));
        } catch (Exception e) {
            log.error("Error executing transformation", e);
            return ResponseEntity.badRequest().build();
        }
    }
}

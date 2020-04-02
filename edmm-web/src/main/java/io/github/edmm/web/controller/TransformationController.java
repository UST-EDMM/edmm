package io.github.edmm.web.controller;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.validation.Valid;

import io.github.edmm.core.transformation.TransformationService;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.utils.Compress;
import io.github.edmm.web.model.TransformationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
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

    public TransformationController(TransformationService transformationService) {
        this.transformationService = transformationService;
    }

    @Operation(summary = "Transforms a given EDMM model to the selected target technology.")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> transform(@Parameter(required = true) @Valid @RequestBody TransformationRequest model) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(model.getInput());
            String input = new String(decodedBytes);
            // Parse model
            DeploymentModel deploymentModel = DeploymentModel.of(input);
            // Transform model
            Path sourceDirectory = Paths.get("C:\\work\\edmm\\getting-started\\2019-icsoc");
            Path targetDirectory = Files.createTempDirectory(model.getTarget() + "-");
            transformationService.transform(deploymentModel, model.getTarget(), sourceDirectory.toFile(), targetDirectory.toFile());
            // Compress output
            Path zipFile = Files.createTempFile(model.getTarget() + "-", ".zip");
            Compress.zip(targetDirectory, zipFile);
            // Prepare response
            Resource response = asResource(zipFile);
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFilename() + "\"")
                .body(response);
        } catch (Exception e) {
            log.error("Error executing transformation", e);
            return ResponseEntity.badRequest().build();
        }
    }

    private Resource asResource(Path file) {
        try {
            Resource resource = new UrlResource(file.normalize().toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new IllegalArgumentException("File not found");
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("File not found", e);
        }
    }
}

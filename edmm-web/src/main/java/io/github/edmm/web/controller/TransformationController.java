package io.github.edmm.web.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.utils.Compress;
import io.github.edmm.web.model.TransformationRequest;
import io.github.edmm.web.model.TransformationResult;
import io.github.edmm.web.service.TransformationHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static io.github.edmm.core.transformation.TransformationContext.State.DONE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/transform")
@Tag(name = "transform", description = "The transformation API")
public class TransformationController {

    private final TransformationHandler transformationHandler;

    public TransformationController(TransformationHandler transformationHandler) {
        this.transformationHandler = transformationHandler;
    }

    @Operation(summary = "Transforms a given EDMM model to the selected target technology.")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Object> transform(@Parameter(required = true) @Valid @RequestBody TransformationRequest model) {
        String id = UUID.randomUUID().toString();
        transformationHandler.doTransform(id, model);
        WebMvcLinkBuilder link = linkTo(methodOn(TransformationController.class).getTask(id));
        return ResponseEntity.created(link.toUri()).build();
    }

    @Operation(summary = "Lists all available transformation tasks.")
    @GetMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TransformationResult> getTasks() {
        return transformationHandler.getTasks().stream()
            .map(TransformationResult::of)
            .collect(Collectors.toList());
    }

    @Operation(summary = "Lists a task that matches the given id.")
    @GetMapping(value = "/tasks/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TransformationResult getTask(@PathVariable String id) {
        Optional<TransformationContext> task = transformationHandler.getTask(id);
        if (!task.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return TransformationResult.of(task.get());
    }

    @Operation(summary = "Downloads the compressed result of a transformation.")
    @GetMapping(value = "/tasks/{id}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadFile(@PathVariable String id) {
        TransformationContext context = transformationHandler.getTask(id).orElse(null);
        if (context != null && DONE.equals(context.getState())) {
            try {
                String name = context.getTargetTechnology().getId();
                Path zipFile = Files.createTempFile(name + "-", ".zip");
                Compress.zip(context.getTargetDirectory().toPath(), zipFile);
                Resource response = asResource(zipFile);
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFilename() + "\"")
                    .body(response);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating compressed output", e);
            }
        }
        return ResponseEntity.notFound().build();
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

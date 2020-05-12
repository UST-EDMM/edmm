package io.github.edmm.web.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.edmm.core.execution.ExecutionContext;
import io.github.edmm.model.parameters.UserInput;
import io.github.edmm.web.model.DeploymentResult;
import io.github.edmm.web.service.DeploymentHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/deploy")
@Tag(name = "deploy", description = "The deployment API")
public class DeploymentController {

    private final DeploymentHandler deploymentHandler;

    public DeploymentController(DeploymentHandler deploymentHandler) {
        this.deploymentHandler = deploymentHandler;
    }

    @Operation(summary = "Triggers the deployment for a given transformation.")
    @PostMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deploy(@PathVariable String id, @RequestBody Set<UserInput> userInputs) {
        deploymentHandler.doDeploy(id, userInputs);
        WebMvcLinkBuilder link = linkTo(methodOn(DeploymentController.class).getTask(id));
        return ResponseEntity.created(link.toUri()).build();
    }

    @Operation(summary = "Lists all available deployment tasks.")
    @GetMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DeploymentResult> getTasks() {
        return deploymentHandler.getTasks().stream().map(DeploymentResult::of).collect(Collectors.toList());
    }

    @Operation(summary = "Lists a task that matches the given id.")
    @GetMapping(value = "/tasks/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeploymentResult getTask(@PathVariable String id) {
        Optional<ExecutionContext> task = deploymentHandler.getTask(id);
        if (!task.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return DeploymentResult.of(task.get());
    }
}

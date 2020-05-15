package io.github.edmm.web.service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.github.edmm.core.execution.ExecutionContext;
import io.github.edmm.core.execution.ExecutionService;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.parameters.UserInput;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static io.github.edmm.core.execution.ExecutionContext.State.ERROR;

@Slf4j
@Service
public class DeploymentHandler {

    private final ExecutionService executionService;
    private final TransformationHandler transformationHandler;
    private final Map<String, ExecutionContext> store = new ConcurrentHashMap<>();

    public DeploymentHandler(ExecutionService executionService, TransformationHandler transformationHandler) {
        this.executionService = executionService;
        this.transformationHandler = transformationHandler;
    }

    public Collection<ExecutionContext> getTasks() {
        return store.values();
    }

    public Optional<ExecutionContext> getTask(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Async
    public void doDeploy(String id, Set<UserInput> userInputs) {
        TransformationContext tc = transformationHandler.getTask(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ExecutionContext context = new ExecutionContext(id, tc, userInputs);
        store.put(id, context);
        try {
            executionService.start(context);
        } catch (Exception e) {
            log.error("Deployment failed: {}", e.getMessage(), e);
            context.setState(ERROR);
            store.put(id, context);
        }
    }
}

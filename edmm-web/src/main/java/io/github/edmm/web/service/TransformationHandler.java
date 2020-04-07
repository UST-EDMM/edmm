package io.github.edmm.web.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationService;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.web.model.TransformationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static io.github.edmm.core.transformation.TransformationContext.State.ERROR;

@Slf4j
@Service
public class TransformationHandler {

    private final TransformationService service;
    private final Map<String, TransformationContext> store = new ConcurrentHashMap<>();

    @Value("${repository.path}")
    private String repositoryPath;

    public TransformationHandler(TransformationService service) {
        this.service = service;
    }

    public Collection<TransformationContext> getTasks() {
        return store.values();
    }

    public Optional<TransformationContext> getTask(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Async
    public void doTransform(String id, TransformationRequest model) {
        TransformationContext context;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(model.getInput());
            String input = new String(decodedBytes);
            DeploymentModel deploymentModel = DeploymentModel.of(input);
            Path sourceDirectory = Paths.get(repositoryPath);
            Path targetDirectory = Files.createTempDirectory(id + "-");
            context = service.createContext(deploymentModel, model.getTarget(), sourceDirectory.toFile(), targetDirectory.toFile());
            context.setId(id);
            store.put(id, context);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create transformation context", e);
        }
        try {
            service.startTransformation(context);
        } catch (Exception e) {
            log.error("Transformation failed: {}", e.getMessage(), e);
            context.setState(ERROR);
            store.put(id, context);
        }
    }
}

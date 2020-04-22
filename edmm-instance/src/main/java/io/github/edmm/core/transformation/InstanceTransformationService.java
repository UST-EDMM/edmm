package io.github.edmm.core.transformation;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.plugin.InstancePluginService;
import io.github.edmm.core.transformation.support.InstanceExecutionTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class InstanceTransformationService {

    private static final Logger logger = LoggerFactory.getLogger(InstanceTransformationService.class);

    private final InstancePluginService instancePluginService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    public InstanceTransformationService(InstancePluginService instancePluginService) {
        this.instancePluginService = instancePluginService;
    }

    public void startTransformation(InstanceTransformationContext context) {
        SourceTechnology sourceTechnology = context.getSourceTechnology();
        Optional<InstancePlugin<?>> plugin = instancePluginService.findBySourceTechnology(sourceTechnology);
        if (!plugin.isPresent()) {
            logger.error("InstancePlugin for given technology '{}' could not be found", sourceTechnology.getId());
            return;
        }
        if (context.getState() == InstanceTransformationContext.State.READY) {
            try {
                executor.submit(new InstanceExecutionTask(plugin.get(), context)).get();
            } catch (Exception e) {
                logger.error("Error executing transformation task", e);
            }
        }
    }

    public InstanceTransformationContext createContext(String source, String path) {
        SourceTechnology sourceTechnology = instancePluginService.getSupportedSourceTechnologies().stream()
            .filter(p -> p.getId().equals(source))
            .findFirst()
            .orElseThrow(IllegalStateException::new);
        return new InstanceTransformationContext(sourceTechnology, path);
    }
}

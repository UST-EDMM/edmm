package io.github.edmm.core.transformation;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.plugin.PluginService;
import io.github.edmm.core.transformation.support.ExecutionTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransformationService {

    private static final Logger logger = LoggerFactory.getLogger(TransformationService.class);

    private final PluginService pluginService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    public TransformationService(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void startTransformation(TransformationContext context) {
        Platform targetPlatform = context.getTargetPlatform();
        Optional<Plugin> plugin = pluginService.findByPlatform(targetPlatform);
        if (!plugin.isPresent()) {
            logger.error("Plugin for given platform '{}' could not be found", targetPlatform.getId());
            return;
        }
        if (context.getState() == TransformationContext.State.READY) {
            try {
                executor.submit(new ExecutionTask(plugin.get(), context)).get();
            } catch (Exception e) {
                logger.error("Error executing transformation task", e);
            }
        }
    }
}

package io.github.edmm.core.execution;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.execution.support.ExecutionTask;
import io.github.edmm.core.plugin.ExecutionPlugin;
import io.github.edmm.core.plugin.PluginService;
import io.github.edmm.core.transformation.TransformationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.github.edmm.core.execution.ExecutionContext.State.READY;

@Service
public class ExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionService.class);

    private final PluginService pluginService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    public ExecutionService(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void start(ExecutionContext context) {
        DeploymentTechnology dt = context.getTransformation().getDeploymentTechnology();
        Optional<ExecutionPlugin> plugin = pluginService.getExecutionPlugin(dt);
        if (!plugin.isPresent()) {
            logger.error("Plugin for given technology '{}' could not be found", dt.getId());
            return;
        }
        if (context.getState() == READY) {
            try {
                executor.submit(new ExecutionTask(plugin.get(), context)).get();
            } catch (Exception e) {
                logger.error("Error executing deployment", e);
            }
        }
    }

    public ExecutionContext createContext(TransformationContext tc) {
        return new ExecutionContext(tc);
    }
}

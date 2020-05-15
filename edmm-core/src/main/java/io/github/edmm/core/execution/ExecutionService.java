package io.github.edmm.core.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.execution.support.ExecutionTask;
import io.github.edmm.core.plugin.ExecutionPlugin;
import io.github.edmm.core.plugin.PluginService;
import io.github.edmm.model.parameters.InputParameter;
import io.github.edmm.model.parameters.ParameterInstance;
import io.github.edmm.model.parameters.UserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.github.edmm.core.execution.ExecutionContext.State.ERROR;
import static io.github.edmm.core.execution.ExecutionContext.State.READY;
import static io.github.edmm.model.parameters.ParameterInstance.isValid;

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
        List<String> errors = new ArrayList<>();
        DeploymentTechnology dt = context.getTransformation().getDeploymentTechnology();
        if (!dt.isDeploymentSupported()) {
            errors.add(String.format("Deployment with '%s' not yet supported", dt.getName()));
        }
        Optional<ExecutionPlugin> plugin = pluginService.getExecutionPlugin(dt);
        if (!plugin.isPresent()) {
            logger.error("Plugin for given technology '{}' could not be found", dt.getId());
            return;
        }
        Set<UserInput> userInputs = context.getUserInputs();
        Set<InputParameter> transformationParameters = dt.getTransformationParameters();
        ParameterInstance.of(userInputs, transformationParameters).forEach(p -> {
            if (!isValid(p)) {
                errors.add(String.format("Parameter '%s' is not valid, must be a valid %s value but is '%s'",
                    p.getName(), p.getType().getName(), p.getValue()));
            }
        });
        if (errors.size() > 0) {
            context.setState(ERROR);
            context.putValue("errors", errors);
        }
        if (context.getState() == READY) {
            try {
                executor.submit(new ExecutionTask(plugin.get(), context)).get();
            } catch (Exception e) {
                logger.error("Error executing deployment", e);
            }
        }
    }
}

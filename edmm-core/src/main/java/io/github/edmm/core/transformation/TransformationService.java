package io.github.edmm.core.transformation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.plugin.PluginService;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.support.TransformationTask;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.parameters.InputParameter;
import io.github.edmm.model.parameters.ParameterInstance;
import io.github.edmm.model.parameters.UserInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.github.edmm.core.transformation.TransformationContext.State.ERROR;
import static io.github.edmm.core.transformation.TransformationContext.State.READY;
import static io.github.edmm.model.parameters.ParameterInstance.isValid;

@Service
public class TransformationService {

    private static final Logger logger = LoggerFactory.getLogger(TransformationService.class);

    private final PluginService pluginService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    public TransformationService(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public void start(TransformationContext context) {
        DeploymentTechnology dt = context.getDeploymentTechnology();
        Optional<TransformationPlugin<?>> plugin = pluginService.getTransformationPlugin(dt);
        if (!plugin.isPresent()) {
            logger.error("Plugin for given technology '{}' could not be found", dt.getId());
            return;
        }
        List<String> errors = new ArrayList<>();
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
                executor.submit(new TransformationTask(plugin.get(), context)).get();
            } catch (Exception e) {
                logger.error("Error executing transformation task", e);
                context.setErrorState(e);
            }
        }
    }

    public TransformationContext createContext(DeploymentModel model, String target, File sourceDirectory, File targetDirectory) {
        DeploymentTechnology deploymentTechnology = pluginService.getSupportedTransformationTargets().stream()
            .filter(p -> p.getId().equals(target)).findFirst()
            .orElseThrow(IllegalStateException::new);
        return new TransformationContext(model, deploymentTechnology, sourceDirectory, targetDirectory);
    }
}

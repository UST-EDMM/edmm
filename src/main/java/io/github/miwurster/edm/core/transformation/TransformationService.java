package io.github.miwurster.edm.core.transformation;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.github.miwurster.edm.core.plugin.Plugin;
import io.github.miwurster.edm.core.plugin.PluginService;
import io.github.miwurster.edm.core.transformation.support.ExecutionTask;
import io.github.miwurster.edm.model.EffectiveModel;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransformationService {

    private static final Logger logger = LoggerFactory.getLogger(TransformationService.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private PluginService pluginService;

    @Autowired
    public TransformationService(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public Transformation createTransformation(@NonNull EffectiveModel model, @NonNull Platform platform) {
        return new Transformation(model, platform);
    }

    public boolean startTransformation(Transformation transformation, File outputDirectory) {
        Platform targetPlatform = transformation.getTargetPlatform();
        Optional<Plugin> plugin = pluginService.findByPlatform(targetPlatform);
        if (!plugin.isPresent()) {
            logger.error("Plugin for given platform '{}' could not be found", targetPlatform.getId());
            return false;
        }
        if (transformation.getState() == Transformation.State.READY) {
            Future<Void> task = executor.submit(
                    new ExecutionTask(plugin.get(), transformation, outputDirectory)
            );
            try {
                task.get();
                return true;
            } catch (Exception e) {
                logger.error("Error executing transformation task", e);
            }
        }
        return false;
    }
}

package io.github.edmm.core.transformation;

import java.io.File;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jgrapht.Graph;
import org.springframework.lang.Nullable;

@Getter
public final class TransformationContext {

    private final DeploymentModel model;
    private final Platform targetPlatform;
    private final File sourceDirectory;
    private final File targetDirectory;

    @Setter
    private State state = State.READY;

    public TransformationContext(DeploymentModel model, Platform targetPlatform) {
        this(model, targetPlatform, null, null);
    }

    public TransformationContext(@NonNull DeploymentModel model, @NonNull Platform targetPlatform,
                                 @Nullable File sourceDirectory, @Nullable File targetDirectory) {
        this.model = model;
        this.targetPlatform = targetPlatform;
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
    }

    public DeploymentModel getModel() {
        return model;
    }

    public Graph<RootComponent, RootRelation> getTopologyGraph() {
        return model.getTopology();
    }

    public PluginFileAccess getFileAccess() {
        return new PluginFileAccess(sourceDirectory, targetDirectory);
    }

    public enum State {
        READY,
        TRANSFORMING,
        DONE,
        ERROR
    }
}

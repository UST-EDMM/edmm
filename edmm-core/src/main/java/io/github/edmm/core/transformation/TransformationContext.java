package io.github.edmm.core.transformation;

import java.io.File;
import java.sql.Timestamp;
import java.util.UUID;

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

    private final String id;
    private final DeploymentModel model;
    private final TargetTechnology targetTechnology;
    private final File sourceDirectory;
    private final File targetDirectory;
    private final Timestamp timestamp;

    @Setter
    private State state = State.READY;

    public TransformationContext(DeploymentModel model, TargetTechnology targetTechnology) {
        this(model, targetTechnology, null, null);
    }

    public TransformationContext(@NonNull DeploymentModel model, @NonNull TargetTechnology targetTechnology,
                                 @Nullable File sourceDirectory, @Nullable File targetDirectory) {
        this(UUID.randomUUID().toString(), model, targetTechnology, sourceDirectory, targetDirectory);
    }

    public TransformationContext(@NonNull String id, @NonNull DeploymentModel model, @NonNull TargetTechnology targetTechnology,
                                 @Nullable File sourceDirectory, @Nullable File targetDirectory) {
        this.id = id;
        this.model = model;
        this.targetTechnology = targetTechnology;
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
        this.timestamp = new Timestamp(System.currentTimeMillis());
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

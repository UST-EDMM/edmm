package io.github.edmm.core.transformation;

import java.io.File;
import java.nio.file.Paths;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import lombok.Getter;
import lombok.NonNull;
import org.jgrapht.Graph;

@Getter
public final class TransformationContext {

    private final Transformation transformation;

    private final File sourceDirectory;
    private final File targetDirectory;

    public TransformationContext(@NonNull Transformation transformation, @NonNull File targetDirectory) {
        this(transformation, Paths.get("").toFile(), targetDirectory);
    }

    public TransformationContext(@NonNull Transformation transformation, @NonNull File sourceDirectory, @NonNull File targetDirectory) {
        this.transformation = transformation;
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
    }

    public DeploymentModel getModel() {
        return transformation.getModel();
    }

    public Graph<RootComponent, RootRelation> getTopologyGraph() {
        return getModel().getTopology();
    }

    public PluginFileAccess getFileAccess() {
        return new PluginFileAccess(sourceDirectory, targetDirectory);
    }
}

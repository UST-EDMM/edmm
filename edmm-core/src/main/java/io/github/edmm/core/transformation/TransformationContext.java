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
    private final File rootDirectory;

    public TransformationContext(@NonNull Transformation transformation, @NonNull File rootDirectory) {
        this.transformation = transformation;
        this.rootDirectory = rootDirectory;
    }

    public DeploymentModel getModel() {
        return transformation.getModel();
    }

    public Graph<RootComponent, RootRelation> getTopologyGraph() {
        return getModel().getTopology();
    }

    /**
     * Creates a {@link PluginFileAccess} object that is able to create and modify files inside the plugin's root
     * directory. It uses the current working directory as the source directory.
     */
    public PluginFileAccess getFileAccess() {
        return new PluginFileAccess(Paths.get("").toFile(), rootDirectory);
    }

    /**
     * Creates a {@link PluginFileAccess} object that is able to create and modify files inside the plugin's root
     * directory. It uses the specified directory as the source directory.
     *
     * @param sourceDirectory The source directory to use
     */
    public PluginFileAccess getFileAccess(File sourceDirectory) {
        return new PluginFileAccess(sourceDirectory, rootDirectory);
    }
}

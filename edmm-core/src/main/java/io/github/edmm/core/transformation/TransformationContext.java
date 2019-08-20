package io.github.edmm.core.transformation;

import java.io.File;

import io.github.edmm.core.plugin.PluginFileAccess;
import lombok.Getter;
import lombok.NonNull;

@Getter
public final class TransformationContext {

    private final Transformation transformation;
    private final File rootDirectory;

    public TransformationContext(@NonNull Transformation transformation, @NonNull File rootDirectory) {
        this.transformation = transformation;
        this.rootDirectory = rootDirectory;
    }

    /**
     * Creates a PluginFileAccess objects that is able to create and modify files inside the plugin's root directory.
     * Further, it is able to use a source directory as an input.
     */
    public PluginFileAccess getFileAccess(File sourceDirectory) {
        return new PluginFileAccess(sourceDirectory, rootDirectory);
    }
}

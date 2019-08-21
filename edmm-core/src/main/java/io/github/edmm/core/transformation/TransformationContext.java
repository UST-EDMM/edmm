package io.github.edmm.core.transformation;

import java.io.File;
import java.nio.file.Paths;

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
     * Creates a {@code PluginFileAccess} object that is able to create and modify files inside the plugin's root
     * directory. Further, it uses the current working directory as the source directory.
     */
    public PluginFileAccess getFileAccess() {
        return new PluginFileAccess(Paths.get("").toFile(), rootDirectory);
    }
}

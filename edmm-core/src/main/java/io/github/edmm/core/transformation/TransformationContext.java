package io.github.edmm.core.transformation;

import java.io.File;

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
}

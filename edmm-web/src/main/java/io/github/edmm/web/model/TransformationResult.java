package io.github.edmm.web.model;

import java.nio.file.Path;

import io.github.edmm.core.transformation.TransformationContext;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public final class TransformationResult {

    private String id;
    private String state;
    private String path;

    public static TransformationResult of(@NonNull String id, @NonNull TransformationContext context, @NonNull Path path) {
        return TransformationResult.builder().id(id)
            .state(context.getState().toString().toLowerCase())
            .path(path.toAbsolutePath().toString())
            .build();
    }
}

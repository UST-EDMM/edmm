package io.github.edmm.web.model;

import io.github.edmm.core.transformation.TransformationContext;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public final class TransformationResult {

    private String state;
    private String outputPath;

    public static TransformationResult of(@NonNull TransformationContext context) {
        return TransformationResult.builder()
                .state(context.getState().toString().toLowerCase())
                .outputPath(context.getTargetDirectory().getAbsolutePath())
                .build();
    }
}

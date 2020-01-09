package io.github.edmm.web.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public final class TransformationResult {

    private String outputPath;

    public static TransformationResult of(@NonNull String outputPath) {
        return TransformationResult.builder()
                .outputPath(outputPath)
                .build();
    }
}

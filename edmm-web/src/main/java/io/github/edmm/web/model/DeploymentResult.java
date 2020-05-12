package io.github.edmm.web.model;

import java.util.Map;

import io.github.edmm.core.execution.ExecutionContext;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class DeploymentResult {

    private final String id;
    private final String state;
    private final Map<String, Object> values;

    public static DeploymentResult of(@NonNull ExecutionContext context) {
        return DeploymentResult.builder()
            .id(context.getId())
            .state(context.getState().toString().toLowerCase())
            .values(context.getValues())
            .build();
    }
}

package io.github.edmm.core.transformation;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public final class InstanceTransformationContext {

    private final String id;
    private final SourceTechnology sourceTechnology;
    private final String outputPath;
    private final Timestamp timestamp;

    @Setter
    private State state = State.READY;

    public InstanceTransformationContext(@NonNull SourceTechnology sourceTechnology, @NonNull String outputPath) {
        this(UUID.randomUUID().toString(), sourceTechnology, outputPath);
    }

    public InstanceTransformationContext(@NonNull String id, @NonNull SourceTechnology sourceTechnology, @NonNull String outputPath) {
        this.id = id;
        this.sourceTechnology = sourceTechnology;
        this.outputPath = outputPath;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public enum State {
        READY,
        TRANSFORMING,
        DONE,
        ERROR
    }
}

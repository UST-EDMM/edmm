package io.github.edmm.core.execution;

import java.io.File;
import java.util.UUID;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.transformation.TransformationContext;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public final class ExecutionContext {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionContext.class);

    private final File directory;
    private final TransformationContext transformation;

    private String id;
    private State state = State.READY;

    public ExecutionContext(TransformationContext tc) {
        this(UUID.randomUUID().toString(), tc);
    }

    public ExecutionContext(String id, TransformationContext tc) {
        this.id = id;
        this.directory = tc.getTargetDirectory();
        this.transformation = tc;
    }

    public PluginFileAccess getFileAccess() {
        return new PluginFileAccess(directory, directory);
    }

    public enum State {
        READY,
        DEPLOYING,
        DONE,
        ERROR
    }
}

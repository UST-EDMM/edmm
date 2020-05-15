package io.github.edmm.core.execution;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.parameters.UserInput;

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

    private Set<UserInput> userInputs;
    private Map<String, Object> values;

    public ExecutionContext(TransformationContext tc) {
        this(UUID.randomUUID().toString(), tc, null);
    }

    public ExecutionContext(TransformationContext tc, Set<UserInput> userInputs) {
        this(tc);
        this.userInputs = userInputs;
    }

    public ExecutionContext(String id, TransformationContext tc, Set<UserInput> userInputs) {
        this.id = id;
        this.directory = tc.getTargetDirectory();
        this.transformation = tc;
        this.userInputs = userInputs;
    }

    public PluginFileAccess getFileAccess() {
        return new PluginFileAccess(directory, directory);
    }

    public void putValue(String name, Object value) {
        if (values == null) {
            values = new HashMap<>();
        }
        values.put(name, value);
    }

    public Object getValue(String name) {
        if (values == null) {
            return null;
        }
        return values.get(name);
    }

    public enum State {
        READY,
        DEPLOYING,
        DONE,
        ERROR
    }
}

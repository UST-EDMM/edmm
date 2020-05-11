package io.github.edmm.core.transformation;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.JsonHelper;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.parameters.UserInput;
import io.github.edmm.model.relation.RootRelation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
public final class TransformationContext {

    public static final String CONTEXT_FILENAME = "edmm.json";

    private String id;
    private File input;
    @JsonIgnore
    private String inputAsString;
    private DeploymentModel model;
    private DeploymentTechnology deploymentTechnology;
    private File sourceDirectory;
    private File targetDirectory;
    private Timestamp timestamp;
    private State state = State.READY;

    private Set<UserInput> userInputs;
    private Map<String, Object> values;

    public TransformationContext(@NonNull DeploymentModel model, @NonNull DeploymentTechnology deploymentTechnology) {
        this(UUID.randomUUID().toString(), model, deploymentTechnology, null, null);
    }

    public TransformationContext(@NonNull DeploymentModel model, @NonNull DeploymentTechnology deploymentTechnology,
                                 @Nullable File sourceDirectory, @Nullable File targetDirectory) {
        this(UUID.randomUUID().toString(), model, deploymentTechnology, sourceDirectory, targetDirectory);
    }

    public TransformationContext(@NonNull File input, @NonNull DeploymentTechnology deploymentTechnology,
                                 @Nullable File sourceDirectory, @Nullable File targetDirectory) {
        this(UUID.randomUUID().toString(), null, deploymentTechnology, sourceDirectory, targetDirectory);
        this.input = input;
    }

    public TransformationContext(@NonNull String inputAsString, @NonNull DeploymentTechnology deploymentTechnology,
                                 @Nullable File sourceDirectory, @Nullable File targetDirectory) {
        this(UUID.randomUUID().toString(), null, deploymentTechnology, sourceDirectory, targetDirectory);
        this.inputAsString = inputAsString;
    }

    private TransformationContext(String id, DeploymentModel model, DeploymentTechnology deploymentTechnology,
                                  @Nullable File sourceDirectory, @Nullable File targetDirectory) {
        this.id = id;
        this.model = model;
        this.deploymentTechnology = deploymentTechnology;
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public static TransformationContext of(File directory) {
        File file = new File(directory, CONTEXT_FILENAME);
        if (!file.isFile() || !directory.canRead()) {
            throw new IllegalStateException(String.format("Cannot read context from file '%s'", file));
        }
        return JsonHelper.readValue(file, TransformationContext.class);
    }

    @JsonIgnore
    public DeploymentModel getModel() {
        if (model == null) {
            if (input == null && StringUtils.isBlank(inputAsString)) {
                throw new IllegalStateException("Context requires either an input as file or as string");
            }
            if (input != null) {
                model = DeploymentModel.of(input);
            } else {
                model = DeploymentModel.of(inputAsString);
            }
        }
        return model;
    }

    @JsonIgnore
    public Graph<RootComponent, RootRelation> getTopologyGraph() {
        return getModel().getTopology();
    }

    @JsonIgnore
    public PluginFileAccess getFileAccess() {
        return new PluginFileAccess(sourceDirectory, targetDirectory);
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
        TRANSFORMING,
        DONE,
        ERROR
    }
}

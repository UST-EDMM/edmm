package io.github.ust.edmm.core.transformation;

import io.github.ust.edmm.model.DeploymentModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Transformation {

    private final DeploymentModel model;
    private final Platform targetPlatform;

    private State state = State.READY;

    public Transformation(@NonNull DeploymentModel model, @NonNull Platform targetPlatform) {
        this.model = model;
        this.targetPlatform = targetPlatform;
    }

    public enum State {
        READY,
        TRANSFORMING,
        DONE,
        ERROR
    }
}

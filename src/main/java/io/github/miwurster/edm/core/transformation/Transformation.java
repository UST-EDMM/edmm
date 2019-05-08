package io.github.miwurster.edm.core.transformation;

import io.github.miwurster.edm.model.EffectiveModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Transformation {

    private final EffectiveModel model;
    private final Platform targetPlatform;

    private State state = State.READY;

    public Transformation(@NonNull EffectiveModel model, @NonNull Platform targetPlatform) {
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

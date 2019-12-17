package io.github.edmm.core.plugin.support;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.component.RootComponent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckModelResult {

    private State state = State.PENDING;

    private final List<RootComponent> unsupportedComponents = new ArrayList<>();

    public CheckModelResult(State state) {
        this.state = state;
    }

    public void addUnsupportedComponent(RootComponent component) {
        unsupportedComponents.add(component);
    }

    public enum State {
        PENDING,
        UNSUPPORTED_COMPONENTS,
        OK,
    }
}

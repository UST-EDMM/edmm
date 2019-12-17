package io.github.edmm.core.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.plugin.support.LifecyclePhaseAccess;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;

public abstract class AbstractLifecycle implements PluginLifecycle, LifecyclePhaseAccess {

    protected final TransformationContext context;

    private final List<LifecyclePhase> phases;

    public AbstractLifecycle(TransformationContext context) {
        this.context = context;
        this.phases = populatePhases();
    }

    @Override
    public CheckModelResult checkModel() {
        return new CheckModelResult(context.getModel().getComponents());
    }

    @Override
    public void prepare() {
        // NOOP
    }

    @Override
    public void cleanup() {
        // NOOP
    }

    private List<LifecyclePhase> populatePhases() {
        List<LifecyclePhase> phases = new ArrayList<>();
        phases.add(new LifecyclePhase<>("check_model", this, (e) -> {
            CheckModelResult.State state = e.checkModel().getState();
            if (CheckModelResult.State.UNSUPPORTED_COMPONENTS.equals(state)) {
                throw new TransformationException("Transformation failed, because the model check has failed");
            }
        }));
        phases.add(new LifecyclePhase<>("prepare", this, PluginLifecycle::prepare));
        phases.add(new LifecyclePhase<>("transformation", this, PluginLifecycle::transform));
        phases.add(new LifecyclePhase<>("cleanup", this, PluginLifecycle::cleanup));
        return Collections.unmodifiableList(phases);
    }

    @Override
    public List<LifecyclePhase> getLifecyclePhases() {
        return phases;
    }
}

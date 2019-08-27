package io.github.edmm.core.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.edmm.core.plugin.support.LifecyclePhaseAccess;

public abstract class AbstractLifecycle implements PluginLifecycle, LifecyclePhaseAccess {

    private final List<LifecyclePhase> phases;

    public AbstractLifecycle() {
        this.phases = populatePhases();
    }

    @Override
    public boolean checkEnvironment() {
        return true;
    }

    @Override
    public boolean checkModel() {
        return true;
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
        phases.add(new LifecyclePhase<>("check_environment", this, (e) -> {
            if (!e.checkEnvironment()) {
                throw new Exception("Transformation failed, because the environment check has failed");
            }
        }));
        phases.add(new LifecyclePhase<>("check_model", this, (e) -> {
            if (!e.checkModel()) {
                throw new Exception("Transformation failed, because the model check has failed");
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

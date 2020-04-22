package io.github.edmm.core.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.edmm.core.plugin.support.InstanceLifecyclePhaseAccess;
import io.github.edmm.core.plugin.support.Phases;
import io.github.edmm.core.transformation.InstanceTransformationContext;

public abstract class AbstractLifecycleInstancePlugin implements InstancePluginLifecycle, InstanceLifecyclePhaseAccess {

    protected final InstanceTransformationContext context;

    private final List<InstanceLifecyclePhase> phases;

    public AbstractLifecycleInstancePlugin(InstanceTransformationContext context) {
        this.context = context;
        this.phases = populatePhases();
    }

    private List<InstanceLifecyclePhase> populatePhases() {
        List<InstanceLifecyclePhase> phases = new ArrayList<>();
        phases.add(new InstanceLifecyclePhase<>(Phases.PREPARE, this, InstancePluginLifecycle::prepare));
        phases.add(new InstanceLifecyclePhase<>(Phases.GET_MODELS, this, InstancePluginLifecycle::getModels));
        phases.add(new InstanceLifecyclePhase<>(Phases.TRANSFORM_EDIMM, this, InstancePluginLifecycle::transformToEDIMM));
        phases.add(new InstanceLifecyclePhase<>(Phases.TRANSFORM_TOSCA, this, InstancePluginLifecycle::transformToTOSCA));
        phases.add(new InstanceLifecyclePhase<>(Phases.CREATE_YAML, this, InstancePluginLifecycle::createYAML));
        phases.add(new InstanceLifecyclePhase<>(Phases.CLEANUP, this, InstancePluginLifecycle::cleanup));
        return Collections.unmodifiableList(phases);
    }

    @Override
    public List<InstanceLifecyclePhase> getInstanceLifecyclePhases() {
        return phases;
    }
}

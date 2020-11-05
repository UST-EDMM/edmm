package io.github.edmm.core.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.edmm.core.transformation.InstanceTransformationContext;

import lombok.Getter;

public abstract class AbstractLifecycleInstancePlugin<L extends InstancePluginLifecycle> implements InstancePluginLifecycle {

    protected final InstanceTransformationContext context;

    @Getter
    private final List<InstanceLifecyclePhase<L>> phases;

    protected AbstractLifecycleInstancePlugin(InstanceTransformationContext context) {
        this.context = context;
        this.phases = populatePhases();
    }

    private List<InstanceLifecyclePhase<L>> populatePhases() {
        List<InstanceLifecyclePhase<L>> phases = new ArrayList<>();
        phases.add(new InstanceLifecyclePhase<>(Phases.PREPARE, this, InstancePluginLifecycle::prepare));
        phases.add(new InstanceLifecyclePhase<>(Phases.GET_MODELS, this, InstancePluginLifecycle::getModels));
//        phases.add(new InstanceLifecyclePhase<>(Phases.TRANSFORM_EDMMi, this, InstancePluginLifecycle::transformToEDMMi));
//        phases.add(new InstanceLifecyclePhase<>(Phases.TRANSFORM_TOSCA, this, InstancePluginLifecycle::transformEdmmiToTOSCA));
        phases.add(new InstanceLifecyclePhase<>(Phases.TRANSFORM_TOSCA, this, InstancePluginLifecycle::transformDirectlyToTOSCA));
        phases.add(new InstanceLifecyclePhase<>(Phases.CREATE_YAML, this, InstancePluginLifecycle::createYAML));
        phases.add(new InstanceLifecyclePhase<>(Phases.CLEANUP, this, InstancePluginLifecycle::cleanup));
        return Collections.unmodifiableList(phases);
    }
}

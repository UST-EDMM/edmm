package io.github.edmm.core.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.edmm.core.transformation.InstanceTransformationContext;

import lombok.Getter;
import org.eclipse.winery.model.tosca.TServiceTemplate;

public abstract class AbstractLifecycleInstancePlugin<L extends InstancePluginLifecycle> implements
        InstancePluginLifecycle {

    @Getter
    protected final InstanceTransformationContext context;

    @Getter
    private final List<InstanceLifecyclePhase<L>> phases;

    private TServiceTemplate myServiceTemplate;

    protected AbstractLifecycleInstancePlugin(InstanceTransformationContext context) {
        this.context = context;
        this.phases = populatePhases();
    }

    private List<InstanceLifecyclePhase<L>> populatePhases() {
        List<InstanceLifecyclePhase<L>> phases = new ArrayList<>();
        phases.add(new InstanceLifecyclePhase<>(Phases.PREPARE, this, InstancePluginLifecycle::prepare));
        phases.add(new InstanceLifecyclePhase<>(Phases.GET_MODELS, this, InstancePluginLifecycle::getModels));
        phases.add(new InstanceLifecyclePhase<>(Phases.TRANSFORM_TOSCA,
                this,
                InstancePluginLifecycle::transformDirectlyToTOSCA));
        phases.add(new InstanceLifecyclePhase<>(Phases.STORE_TRANSFORMED_TOSCA,
                this,
                InstancePluginLifecycle::storeTransformedTOSCA,
                context -> !context.isMultiTransform()));
        phases.add(new InstanceLifecyclePhase<>(Phases.CLEANUP, this, InstancePluginLifecycle::cleanup));
        return Collections.unmodifiableList(phases);
    }

    public void updateGeneratedServiceTemplate(TServiceTemplate aGeneratedServiceTemplate) {
        myServiceTemplate = aGeneratedServiceTemplate;
    }

    public TServiceTemplate retrieveGeneratedServiceTemplate() {
        return myServiceTemplate;
    }
}

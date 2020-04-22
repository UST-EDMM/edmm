package io.github.edmm.core.plugin.support;

import io.github.edmm.core.plugin.InstanceLifecyclePhase;

import java.util.List;

public interface InstanceLifecyclePhaseAccess {

    List<InstanceLifecyclePhase> getInstanceLifecyclePhases();
}

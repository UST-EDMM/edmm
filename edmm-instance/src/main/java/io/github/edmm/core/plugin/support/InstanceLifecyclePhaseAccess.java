package io.github.edmm.core.plugin.support;

import java.util.List;

import io.github.edmm.core.plugin.InstanceLifecyclePhase;

public interface InstanceLifecyclePhaseAccess {

    List<InstanceLifecyclePhase> getInstanceLifecyclePhases();
}

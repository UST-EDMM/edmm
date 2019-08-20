package io.github.edmm.core.plugin.support;

import java.util.List;

import io.github.edmm.core.plugin.LifecyclePhase;

public interface LifecyclePhaseAccess {

    List<LifecyclePhase> getLifecyclePhases();
}

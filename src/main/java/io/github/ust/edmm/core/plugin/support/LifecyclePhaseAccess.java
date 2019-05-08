package io.github.ust.edmm.core.plugin.support;

import java.util.List;

import io.github.ust.edmm.core.plugin.LifecyclePhase;

public interface LifecyclePhaseAccess {

    List<LifecyclePhase> getLifecyclePhases();
}

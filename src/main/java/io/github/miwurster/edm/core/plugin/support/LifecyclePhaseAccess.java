package io.github.miwurster.edm.core.plugin.support;

import java.util.List;

import io.github.miwurster.edm.core.plugin.LifecyclePhase;

public interface LifecyclePhaseAccess {

    List<LifecyclePhase> getLifecyclePhases();
}

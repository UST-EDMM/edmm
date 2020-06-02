package io.github.edmm.plugins.puppet.api;

import io.github.edmm.core.plugin.ApiInteractor;
import io.github.edmm.plugins.puppet.model.Master;

public class ApiInteractorImpl implements ApiInteractor {
    private Master master;

    public ApiInteractorImpl(Master master) {
        this.master = master;
    }

    @Override
    public Object getDeployment() {
        return null;
    }

    @Override
    public Object getComponents() {
        return null;
    }

    @Override
    public Object getModel() {
        return null;
    }
}

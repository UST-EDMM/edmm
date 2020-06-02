package io.github.edmm.plugins.puppet.api;

import java.util.List;

import io.github.edmm.core.plugin.ApiInteractor;
import io.github.edmm.plugins.puppet.model.Master;
import io.github.edmm.plugins.puppet.model.Node;

public class ApiInteractorImpl implements ApiInteractor {
    private Master master;

    public ApiInteractorImpl(Master master) {
        this.master = master;
    }

    @Override
    public Master getDeployment() {
        return this.master;
    }

    @Override
    public List<Node> getComponents() {
        return this.master.getNodes();
    }

    @Override
    public Object getModel() {
        return null;
    }
}

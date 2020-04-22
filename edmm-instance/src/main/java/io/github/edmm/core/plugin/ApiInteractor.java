package io.github.edmm.core.plugin;

import io.kubernetes.client.models.V1Deployment;

public interface ApiInteractor {
    Object getDeployment();
    Object getComponents();
    Object getModel();

}

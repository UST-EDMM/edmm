package io.github.edmm.plugins.kubernetes.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.edmm.core.plugin.ApiInteractor;
import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.plugins.kubernetes.util.KubernetesConstants;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1DeploymentList;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;

public class ApiInteractorImpl implements ApiInteractor {

    private final AppsV1Api appsApi;
    private final CoreV1Api coreApi;
    private final String inputDeploymentName;
    private V1Deployment deployment;

    public ApiInteractorImpl(AppsV1Api appsApi, CoreV1Api coreApi, String inputDeploymentName) {
        this.appsApi = appsApi;
        this.coreApi = coreApi;
        this.inputDeploymentName = inputDeploymentName;
    }

    @Override
    public V1Deployment getDeployment() {
        try {
            V1DeploymentList allDeployments = this.appsApi.listDeploymentForAllNamespaces(null, null,
                null, null, null, null, null,
                null, null);
            if (this.inputDeploymentName != null) {
                this.deployment = allDeployments.getItems().stream().filter(depl ->
                    depl.getMetadata().getName().equals(this.inputDeploymentName))
                    .findFirst().orElseThrow(InstanceTransformationException::new);
            } else {
                this.deployment = allDeployments.getItems().stream().findFirst().orElseThrow(InstanceTransformationException::new);
            }
        } catch (ApiException e) {
            throw new InstanceTransformationException("Unable to retrieve deployment.", e.getCause());
        }

        return this.deployment;
    }

    @Override
    public List<V1Pod> getComponents() {
        List<V1Pod> podsOfDeployment = new ArrayList<>();
        try {
            V1PodList podList = this.coreApi.listPodForAllNamespaces(null, null,
                null, null, null, null,
                null, null, null);
            podsOfDeployment = podList.getItems().stream().filter(pod ->
                pod.getMetadata().getLabels().get(KubernetesConstants.APP) != null
                    && pod.getMetadata().getLabels().get(KubernetesConstants.APP)
                    .equals(this.deployment.getMetadata().getLabels().get(KubernetesConstants.APP)))
                .collect(Collectors.toList());
        } catch (ApiException e) {
            throw new InstanceTransformationException("Unable to retrieve components of deployment.", e.getCause());
        }

        return podsOfDeployment;
    }

    @Override
    public Object getModel() {
        return null;
    }
}

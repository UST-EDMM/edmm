package io.github.edmm.plugins.kubernetes.util;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.InstanceProperty;

import io.kubernetes.client.models.V1Container;
import io.kubernetes.client.models.V1PodStatus;

class KubernetesPodPropertiesHandler {

    private final List<InstanceProperty> instanceProperties = new ArrayList<>();
    private final V1PodStatus podStatus;
    private final V1Container container;

    KubernetesPodPropertiesHandler(V1PodStatus podStatus, V1Container container) {
        this.podStatus = podStatus;
        this.container = container;
    }

    List<InstanceProperty> getComponentInstanceProperties() {
        handleProperties();
        setPropertyKeys();

        return this.instanceProperties;
    }

    private void handleProperties() {
        handlePodIP();
        handleImage();
    }

    private void setPropertyKeys() {
        EDMMPropertyMapperImplementation propertyKeyMapper = new EDMMPropertyMapperImplementation();
        this.instanceProperties.forEach(instanceProperty -> instanceProperty.setKey(propertyKeyMapper.toComputePropertyKey(instanceProperty.getKey())));
    }

    private void handlePodIP() {
        if (this.podStatus.getPodIP() != null) {
            this.instanceProperties.add(new InstanceProperty(
                KubernetesConstants.POD_IP,
                this.podStatus.getPodIP().getClass().getSimpleName(),
                this.podStatus.getPodIP())
            );
        }
    }

    private void handleImage() {
        if (this.container.getImage() != null) {
            this.instanceProperties.add(new InstanceProperty(
                KubernetesConstants.IMAGE,
                String.class.getSimpleName(),
                this.container.getImage()
            ));
        }
    }
}

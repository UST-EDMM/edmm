package io.github.edmm.plugins.kubernetes.util;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.ComponentType;
import io.github.edmm.model.edimm.InstanceProperty;

import io.kubernetes.client.models.V1Pod;

public class KubernetesPodsHandler {
    public static List<ComponentInstance> getComponentInstances(List<V1Pod> podList) {
        List<ComponentInstance> componentInstances = new ArrayList<>();
        podList.forEach(pod -> {
            // TODO artifacts, relation instance
            ComponentInstance componentInstance = new ComponentInstance();
            componentInstance.setName(pod.getMetadata().getName());
            // all pods are of type compute
            componentInstance.setType(ComponentType.Compute);
            componentInstance.setId(pod.getMetadata().getUid());
            componentInstance.setCreatedAt(String.valueOf(pod.getMetadata().getCreationTimestamp()));
            componentInstance.setState(KubernetesStateHandler.getComponentInstanceState(pod.getStatus()));
            componentInstance.setMetadata(new KubernetesMetadataHandler(pod.getMetadata()).getMetadata(pod.getApiVersion(), pod.getKind()));
            componentInstance.setInstanceProperties(new KubernetesPodPropertiesHandler(pod.getStatus()).getComponentInstanceProperties());
            // set property with original type string in order to avoid losing this info since we map to EDMM types
            componentInstance.getInstanceProperties().add(new InstanceProperty("type", String.class.getSimpleName(), pod.getMetadata().getLabels().get(KubernetesConstants.APP)));

            componentInstances.add(componentInstance);
        });
        return componentInstances;
    }
}

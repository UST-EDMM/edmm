package io.github.edmm.plugins.kubernetes.util;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.ComponentInstance;
import io.kubernetes.client.models.V1Pod;

public class KubernetesPodsHandler {
    public static List<ComponentInstance> getComponentInstances(List<V1Pod> podList) {
        List<ComponentInstance> componentInstances = new ArrayList<>();
        podList.forEach(pod -> {
            // TODO artifacts, operations, description, relation instance
            ComponentInstance componentInstance = new ComponentInstance();
            componentInstance.setName(pod.getMetadata().getName());
            componentInstance.setType(pod.getMetadata().getLabels().get(KubernetesConstants.APP));
            componentInstance.setId(pod.getMetadata().getUid());
            componentInstance.setCreatedAt(String.valueOf(pod.getMetadata().getCreationTimestamp()));
            componentInstance.setState(KubernetesStateHandler.getComponentInstanceState(pod.getStatus()));
            componentInstance.setMetadata(new KubernetesMetadataHandler(pod.getMetadata()).getMetadata());
            componentInstance.setInstanceProperties(new KubernetesPodPropertiesHandler(pod.getStatus()).getComponentInstanceProperties());

            componentInstances.add(componentInstance);
        });
        return componentInstances;
    }
}

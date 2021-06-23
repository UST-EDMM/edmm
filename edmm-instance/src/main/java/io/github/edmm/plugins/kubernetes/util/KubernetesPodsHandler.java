package io.github.edmm.plugins.kubernetes.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.ComponentType;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.util.Constants;

import io.kubernetes.client.models.V1Pod;

public class KubernetesPodsHandler {
    public static List<ComponentInstance> getComponentInstances(List<V1Pod> podList) {
        List<ComponentInstance> componentInstances = new ArrayList<>();
        podList.forEach(pod -> {
            pod.getSpec().getContainers().forEach(container -> {
                ComponentInstance componentInstance = new ComponentInstance();
                componentInstance.setName(container.getName());
                componentInstance.setType(ComponentType.Compute);
                componentInstance.setId(String.valueOf(UUID.randomUUID().hashCode()));
                componentInstance.setState(KubernetesStateHandler.getComponentInstanceState(pod.getStatus()));
                componentInstance.setCreatedAt(String.valueOf(pod.getMetadata().getCreationTimestamp()));
                componentInstance.setInstanceProperties(new KubernetesPodPropertiesHandler(pod.getStatus(),
                    container).getComponentInstanceProperties());
                componentInstance.getInstanceProperties()
                    .add(new InstanceProperty(Constants.TYPE,
                        String.class.getSimpleName(),
                        container.getImage().split(":")[0]));
                componentInstances.add(componentInstance);
            });
        });
        return componentInstances;
    }
}

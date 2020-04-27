package io.github.edmm.plugins.kubernetes.util;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.edmm.model.Metadata;
import io.kubernetes.client.models.V1ObjectMeta;

public class KubernetesMetadataHandler {

    public static Metadata getMetadata(V1ObjectMeta kubernetesMetadata) {
        Map<String, Object> metadataMap = new LinkedHashMap<>();
        if (kubernetesMetadata.getAnnotations() != null) {
            kubernetesMetadata.getAnnotations().forEach((key, value) -> {
                if (!key.equals(KubernetesConstants.LAST_APPLIED_CONFIG)) {
                    metadataMap.put(key, value);
                }
            });
        }
        if (kubernetesMetadata.getClusterName() != null) {
            metadataMap.put(KubernetesConstants.CLUSTER_NAME, kubernetesMetadata.getClusterName());
        }
        if (kubernetesMetadata.getDeletionGracePeriodSeconds() != null) {
            metadataMap.put(KubernetesConstants.DELETION_GRACE_PERIOD_SECONDS, kubernetesMetadata.getDeletionGracePeriodSeconds());
        }
        if (kubernetesMetadata.getDeletionTimestamp() != null) {
            metadataMap.put(KubernetesConstants.DELETION_TIMESTAMP, kubernetesMetadata.getDeletionTimestamp());
        }
        if (kubernetesMetadata.getFinalizers() != null) {
            metadataMap.put(KubernetesConstants.FINALIZERS, kubernetesMetadata.getFinalizers());
        }
        if (kubernetesMetadata.getGenerateName() != null) {
            metadataMap.put(KubernetesConstants.GENERATE_NAME, kubernetesMetadata.getGenerateName());
        }
        if (kubernetesMetadata.getGeneration() != null) {
            metadataMap.put(KubernetesConstants.GENERATION, kubernetesMetadata.getGeneration());
        }
        if (kubernetesMetadata.getInitializers() != null) {
            metadataMap.put(KubernetesConstants.INITIALIZERS, kubernetesMetadata.getInitializers());
        }
        if (kubernetesMetadata.getLabels() != null) {
            kubernetesMetadata.getLabels().forEach(metadataMap::put);
        }
        if (kubernetesMetadata.getNamespace() != null) {
            metadataMap.put(KubernetesConstants.NAMESPACE, kubernetesMetadata.getNamespace());
        }
        if (kubernetesMetadata.getResourceVersion() != null) {
            metadataMap.put(KubernetesConstants.RESOURCE_VERSION, kubernetesMetadata.getResourceVersion());
        }

        return Metadata.of(metadataMap);
    }
}

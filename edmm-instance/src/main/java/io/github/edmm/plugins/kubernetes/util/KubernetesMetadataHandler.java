package io.github.edmm.plugins.kubernetes.util;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.edmm.model.Metadata;

import io.kubernetes.client.models.V1ObjectMeta;

public class KubernetesMetadataHandler {
    private final Map<String, Object> metadataMap = new LinkedHashMap<>();
    private final V1ObjectMeta kubernetesMetadata;

    public KubernetesMetadataHandler(V1ObjectMeta kubernetesMetadata) {
        this.kubernetesMetadata = kubernetesMetadata;
    }

    private static boolean isMetadata(String key) {
        return !key.equals(KubernetesConstants.LAST_APPLIED_CONFIG);
    }

    public Metadata getMetadata(String apiVersion, String kind) {
        handleApiVersion(apiVersion);
        handleKind(kind);
        handleAnnotations();
        handleClusterName();
        handleDeletionPeriod();
        handleDeletionTimestamp();
        handleFinalizers();
        handleGenerateName();
        handleGeneration();
        handleInitializers();
        handleLabels();
        handleNamespace();
        handleResourceVersion();

        return Metadata.of(this.metadataMap);
    }

    private void handleApiVersion(String apiVersion) {
        if (apiVersion != null) {
            this.metadataMap.put(KubernetesConstants.API_VERSION, apiVersion);
        }
    }

    private void handleKind(String kind) {
        if (kind != null) {
            this.metadataMap.put(KubernetesConstants.KIND, kind);
        }
    }

    private void handleAnnotations() {
        if (this.kubernetesMetadata.getAnnotations() != null && !this.kubernetesMetadata.getAnnotations().isEmpty()) {
            this.kubernetesMetadata.getAnnotations().forEach((key, value) -> {
                if (isMetadata(key)) {
                    this.metadataMap.put(key, value);
                }
            });
        }
    }

    private void handleClusterName() {
        if (this.kubernetesMetadata.getClusterName() != null) {
            this.metadataMap.put(KubernetesConstants.CLUSTER_NAME, this.kubernetesMetadata.getClusterName());
        }
    }

    private void handleDeletionPeriod() {
        if (kubernetesMetadata.getDeletionGracePeriodSeconds() != null) {
            this.metadataMap.put(KubernetesConstants.DELETION_GRACE_PERIOD_SECONDS, kubernetesMetadata.getDeletionGracePeriodSeconds());
        }
    }

    private void handleDeletionTimestamp() {
        if (kubernetesMetadata.getDeletionTimestamp() != null) {
            this.metadataMap.put(KubernetesConstants.DELETION_TIMESTAMP, kubernetesMetadata.getDeletionTimestamp());
        }
    }

    private void handleFinalizers() {
        if (kubernetesMetadata.getFinalizers() != null && !kubernetesMetadata.getFinalizers().isEmpty()) {
            this.metadataMap.put(KubernetesConstants.FINALIZERS, kubernetesMetadata.getFinalizers());
        }
    }

    private void handleGenerateName() {
        if (kubernetesMetadata.getGenerateName() != null) {
            this.metadataMap.put(KubernetesConstants.GENERATE_NAME, kubernetesMetadata.getGenerateName());
        }
    }

    private void handleGeneration() {
        if (kubernetesMetadata.getGeneration() != null) {
            this.metadataMap.put(KubernetesConstants.GENERATION, kubernetesMetadata.getGeneration());
        }
    }

    private void handleInitializers() {
        if (kubernetesMetadata.getInitializers() != null) {
            this.metadataMap.put(KubernetesConstants.INITIALIZERS, kubernetesMetadata.getInitializers());
        }
    }

    private void handleLabels() {
        if (kubernetesMetadata.getLabels() != null && !kubernetesMetadata.getLabels().isEmpty()) {
            kubernetesMetadata.getLabels().forEach(this.metadataMap::put);
        }
    }

    private void handleNamespace() {
        if (kubernetesMetadata.getNamespace() != null) {
            this.metadataMap.put(KubernetesConstants.NAMESPACE, kubernetesMetadata.getNamespace());
        }
    }

    private void handleResourceVersion() {
        if (kubernetesMetadata.getResourceVersion() != null) {
            this.metadataMap.put(KubernetesConstants.RESOURCE_VERSION, kubernetesMetadata.getResourceVersion());
        }
    }
}

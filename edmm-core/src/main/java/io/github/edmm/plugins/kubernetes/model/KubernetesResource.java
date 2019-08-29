package io.github.edmm.plugins.kubernetes.model;

public interface KubernetesResource {

    void build();

    String toYaml();

    String getName();
}

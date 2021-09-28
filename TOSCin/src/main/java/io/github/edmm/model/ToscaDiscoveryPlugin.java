package io.github.edmm.model;

import java.util.List;

import io.github.edmm.core.transformation.SourceTechnology;

public class ToscaDiscoveryPlugin {
    private String id;
    private SourceTechnology sourceTechnology;
    private List<String> discoveredIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SourceTechnology getSourceTechnology() {
        return sourceTechnology;
    }

    public void setSourceTechnology(SourceTechnology sourceTechnology) {
        this.sourceTechnology = sourceTechnology;
    }

    public List<String> getDiscoveredIds() {
        return discoveredIds;
    }

    public void setDiscoveredIds(List<String> discoveredIds) {
        this.discoveredIds = discoveredIds;
    }
}

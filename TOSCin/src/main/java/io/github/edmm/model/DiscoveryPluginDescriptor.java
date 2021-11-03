package io.github.edmm.model;

import java.util.List;

import io.github.edmm.core.transformation.SourceTechnology;

public class DiscoveryPluginDescriptor {
    private String id;
    private List<String> discoveredIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getDiscoveredIds() {
        return discoveredIds;
    }

    public void setDiscoveredIds(List<String> discoveredIds) {
        this.discoveredIds = discoveredIds;
    }
}

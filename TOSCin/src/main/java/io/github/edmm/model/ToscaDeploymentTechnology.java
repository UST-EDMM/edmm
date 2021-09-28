package io.github.edmm.model;

import java.util.List;
import java.util.Map;

import io.github.edmm.core.transformation.SourceTechnology;

public class ToscaDeploymentTechnology {
    private String id;
    private SourceTechnology sourceTechnology;
    private List<String> managedIds;
    private Map<String, String> properties;

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

    public List<String> getManagedIds() {
        return managedIds;
    }

    public void setManagedIds(List<String> managedIds) {
        this.managedIds = managedIds;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}

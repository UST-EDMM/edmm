package io.github.edmm.model;

import java.util.List;
import java.util.Map;

import io.github.edmm.core.transformation.SourceTechnology;

public class ToscaDeploymentTechnology {
    private String id;
    private SourceTechnology sourceTechnology;
    private List<String> infraManagedIds;
    private List<String> appManagedIds;
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

    public List<String> getInfraManagedIds() {
        return infraManagedIds;
    }

    public void setInfraManagedIds(List<String> infraManagedIds) {
        this.infraManagedIds = infraManagedIds;
    }

    public List<String> getAppManagedIds() {
        return appManagedIds;
    }

    public void setAppManagedIds(List<String> appManagedIds) {
        this.appManagedIds = appManagedIds;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}

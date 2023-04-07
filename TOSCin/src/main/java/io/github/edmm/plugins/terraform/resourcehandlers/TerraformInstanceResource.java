package io.github.edmm.plugins.terraform.resourcehandlers;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TerraformInstanceResource<T> {
    private String mode;
    private String type;
    private String provider;
    private String name;
    private List<TerraformInstance<T>> instances;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TerraformInstance<T>> getInstances() {
        return instances;
    }

    public void setInstances(List<TerraformInstance<T>> instances) {
        this.instances = instances;
    }
}

package io.github.edmm.plugins.terraform.resourcehandlers.ec2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EC2Instance {
    private String schemaVersion;
    private EC2InstanceAttributes attributes;

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public EC2InstanceAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(EC2InstanceAttributes attributes) {
        this.attributes = attributes;
    }
}

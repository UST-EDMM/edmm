package io.github.edmm.plugins.terraform.model;

public class TerraformBackendInfo {
    private final String operatingSystem;
    private final String operatingSystemVersion;
    private final String terraformVersion;

    public TerraformBackendInfo(
        String operatingSystem, String operatingSystemVersion, String terraformVersion) {
        this.operatingSystem = operatingSystem;
        this.operatingSystemVersion = operatingSystemVersion;
        this.terraformVersion = terraformVersion;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getOperatingSystemVersion() {
        return operatingSystemVersion;
    }

    public String getTerraformVersion() {
        return terraformVersion;
    }
}

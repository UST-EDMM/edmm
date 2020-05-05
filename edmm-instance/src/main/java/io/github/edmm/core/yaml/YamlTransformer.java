package io.github.edmm.core.yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.core.yaml.support.ConfigurationModelRepresenter;
import io.github.edmm.core.yaml.support.YamlConstants;
import io.github.edmm.core.yaml.support.YamlSupport;
import io.github.edmm.model.edimm.DeploymentInstance;

import org.yaml.snakeyaml.Yaml;

public class YamlTransformer {

    private final Map<String, Object> yamlContent = new LinkedHashMap<>();
    private Yaml yaml;
    private DeploymentInstance deploymentInstance;
    private String fileOutputLocation;

    public void createYamlforEDiMM(DeploymentInstance deploymentInstance, String location) {
        this.deploymentInstance = deploymentInstance;
        prepareContentForYaml();
        generateOutputLocation(location);
        writeYaml();
    }

    private void writeYaml() {
        try {
            createYamlWithRepresenter();
            FileWriter writer = createWriter(this.fileOutputLocation);
            dumpYaml(this.yamlContent, writer);
            createFile(this.fileOutputLocation);
        } catch (IOException e) {
            throw new InstanceTransformationException("Failed to create YAML file due to: " + e.getMessage());
        }
    }

    private void prepareContentForYaml() {
        this.yamlContent.put(YamlConstants.NAME, this.deploymentInstance.getName());
        this.yamlContent.put(YamlConstants.VERSION, this.deploymentInstance.getVersion());
        this.yamlContent.put(YamlConstants.STATE, String.valueOf(this.deploymentInstance.getState()));
        this.yamlContent.put(YamlConstants.ID, this.deploymentInstance.getId());
        this.yamlContent.put(YamlConstants.CREATED_AT, this.deploymentInstance.getCreatedAt());
        this.yamlContent.put(YamlConstants.DESCRIPTION, this.deploymentInstance.getDescription());
        this.yamlContent.put(YamlConstants.METADATA, this.deploymentInstance.getMetadata());
        this.yamlContent.put(YamlConstants.INSTANCE_PROPERTIES, this.deploymentInstance.getInstanceProperties());
        this.yamlContent.put(YamlConstants.COMPONENT_INSTANCES, this.deploymentInstance.getComponentInstances());
    }

    private void generateOutputLocation(String location) {
        this.fileOutputLocation = location + this.deploymentInstance.getName() + YamlConstants.EDIMM
            + Instant.now().getEpochSecond() + YamlConstants.YAML_FILE_SUFFIX;
    }

    private void createYamlWithRepresenter() {
        this.yaml = new Yaml(ConfigurationModelRepresenter.getConfiguredRepresenter(), new YamlSupport().getYamlOptions());
    }

    private FileWriter createWriter(String fileOutputLocation) throws IOException {
        return new FileWriter(fileOutputLocation);
    }

    private void dumpYaml(Map<String, Object> yamlContent, FileWriter writer) {
        this.yaml.dump(yamlContent, writer);
    }

    private void createFile(String fileOutputLocation) {
        File file = new File(fileOutputLocation);
    }

    public String getFileOutputLocation() {
        return this.fileOutputLocation;
    }
}

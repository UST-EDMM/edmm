package io.github.edmm.core.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import io.github.edmm.core.parser.support.ConfigurationModelRepresenter;
import io.github.edmm.core.parser.support.YamlConstants;
import io.github.edmm.core.parser.support.YamlSupport;
import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.model.edimm.DeploymentInstance;
import org.yaml.snakeyaml.Yaml;

public class YamlTransformer {
    /**
     * Create a YAML file for an EDiMM.
     *
     * @param deploymentInstance: DOM representation of EDiMM
     * @param location:           path to store result yaml as passed by the user
     * @return location where yaml was saved
     */
    public String createYamlforEDiMM(DeploymentInstance deploymentInstance, String location) {
        // create output location path + name of file
        String fileOutputLocation = location + deploymentInstance.getName() + YamlConstants.EDIMM
            + Instant.now().getEpochSecond() + YamlConstants.YAML_FILE_SUFFIX;

        return writeYaml(prepareContentForYaml(deploymentInstance), fileOutputLocation);
    }

    private Map<String, Object> prepareContentForYaml(DeploymentInstance deploymentInstance) {
        Map<String, Object> yamlContent = new LinkedHashMap<>();
        // add all information of our deployment instance to data map
        yamlContent.put(YamlConstants.NAME, deploymentInstance.getName());
        yamlContent.put(YamlConstants.VERSION, deploymentInstance.getVersion());
        yamlContent.put(YamlConstants.STATE, String.valueOf(deploymentInstance.getState()));
        yamlContent.put(YamlConstants.ID, deploymentInstance.getId());
        yamlContent.put(YamlConstants.CREATED_AT, deploymentInstance.getCreatedAt());
        yamlContent.put(YamlConstants.DESCRIPTION, deploymentInstance.getDescription());
        yamlContent.put(YamlConstants.METADATA, deploymentInstance.getMetadata());
        yamlContent.put(YamlConstants.INSTANCE_PROPERTIES, deploymentInstance.getInstanceProperties());
        yamlContent.put(YamlConstants.COMPONENT_INSTANCES, deploymentInstance.getComponentInstances());

        return yamlContent;
    }

    private String writeYaml(Map<String, Object> yamlContent, String fileOutputLocation) {
        try {
            Yaml yaml = new Yaml(ConfigurationModelRepresenter.getRepresenter(), YamlSupport.getYamlOptions());
            FileWriter writer = new FileWriter(fileOutputLocation);
            // write data to yaml file
            yaml.dump(yamlContent, writer);
            File file = new File(fileOutputLocation);
            file.setReadOnly();
            return fileOutputLocation;
        } catch (IOException e) {
            throw new InstanceTransformationException("Failed to create YAML file due to: " + e.getMessage());
        }
    }
}

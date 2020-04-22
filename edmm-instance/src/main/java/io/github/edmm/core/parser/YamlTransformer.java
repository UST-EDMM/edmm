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
    public String createYAMLforEDiMM(DeploymentInstance deploymentInstance, String location) {
        Map<String, Object> data = new LinkedHashMap<>();
        // add all information of our deployment instance to data map
        data.put(YamlConstants.NAME, deploymentInstance.getName());
        data.put(YamlConstants.VERSION, deploymentInstance.getVersion());
        data.put(YamlConstants.STATE, String.valueOf(deploymentInstance.getState()));
        data.put(YamlConstants.ID, deploymentInstance.getId());
        data.put(YamlConstants.CREATED_AT, deploymentInstance.getCreatedAt());
        data.put(YamlConstants.DESCRIPTION, deploymentInstance.getDescription());
        data.put(YamlConstants.METADATA, deploymentInstance.getMetadata());
        data.put(YamlConstants.INSTANCE_PROPERTIES, deploymentInstance.getInstanceProperties());
        data.put(YamlConstants.COMPONENT_INSTANCES, deploymentInstance.getComponentInstances());

        // create output location path + name of file
        String fileOutputLocation = location + deploymentInstance.getName() + YamlConstants.EDIMM
            + Instant.now().getEpochSecond() + YamlConstants.YAML_FILE_SUFFIX;
        try {
            Yaml yaml = new Yaml(ConfigurationModelRepresenter.getRepresenter(), YamlSupport.getYamlOptions());
            FileWriter writer = new FileWriter(fileOutputLocation);
            // write data to yaml file
            yaml.dump(data, writer);
            File file = new File(fileOutputLocation);
            file.setReadOnly();
        } catch (IOException e) {
            throw new InstanceTransformationException("Failed to create YAML file due to: " + e.getMessage());
        }

        return fileOutputLocation;
    }
}

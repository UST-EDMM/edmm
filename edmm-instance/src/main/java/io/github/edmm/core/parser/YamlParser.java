package io.github.edmm.core.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import io.github.edmm.core.parser.support.YamlConstants;
import io.github.edmm.core.parser.support.YamlSupport;
import io.github.edmm.model.Metadata;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.edimm.InstanceState;
import org.yaml.snakeyaml.Yaml;

import static org.apache.commons.collections4.MapUtils.emptyIfNull;

public class YamlParser {

    public DeploymentInstance parseYamlAndTransformToDeploymentInstance(String fileInput) {
        Yaml yaml = new Yaml();
        InputStream input = null;
        try {
            input = new FileInputStream(new File(fileInput));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, Object> data = yaml.load(input);
        DeploymentInstance deploymentInstance = new DeploymentInstance();

        deploymentInstance.setName(data.get(YamlConstants.NAME) != null ? String.valueOf(data.get(YamlConstants.NAME)) : null);
        deploymentInstance.setVersion(data.get(YamlConstants.VERSION) != null ? String.valueOf(data.get(YamlConstants.VERSION)) : null);
        deploymentInstance.setState(data.get(YamlConstants.STATE) != null ? InstanceState.InstanceStateForDeploymentInstance.valueOf(String.valueOf(data.get(YamlConstants.STATE))) : null);
        deploymentInstance.setId(data.get(YamlConstants.ID) != null ? String.valueOf(data.get(YamlConstants.ID)) : null);
        deploymentInstance.setCreatedAt(data.get(YamlConstants.CREATED_AT) != null ? String.valueOf(data.get(YamlConstants.CREATED_AT)) : null);
        deploymentInstance.setDescription(data.get(YamlConstants.DESCRIPTION) != null ? String.valueOf(data.get(YamlConstants.DESCRIPTION)) : null);
        deploymentInstance.setMetadata(data.get(YamlConstants.METADATA) != null ? Metadata.of(emptyIfNull((Map<String, Object>) data.get(YamlConstants.METADATA))) : null);
        deploymentInstance.setInstanceProperties(data.get(YamlConstants.INSTANCE_PROPERTIES) != null ? YamlSupport.getInstancePropertiesFromYamlContent(data) : null);
        deploymentInstance.setComponentInstances(data.get(YamlConstants.COMPONENT_INSTANCES) != null ? YamlSupport.getComponentInstancesFromYamlContent(data) : null);

        return deploymentInstance;
    }
}

package io.github.edmm.core.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import io.github.edmm.core.yaml.support.YamlConstants;
import io.github.edmm.core.yaml.support.YamlSupport;
import io.github.edmm.model.Metadata;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.edimm.InstanceState;
import io.github.edmm.util.Util;
import org.yaml.snakeyaml.Yaml;

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
        deploymentInstance.setMetadata(data.get(YamlConstants.METADATA) != null ? Metadata.of(Util.safelyCastToStringObjectMap(data.get(YamlConstants.METADATA))) : Metadata.of(Collections.emptyMap()));
        deploymentInstance.setInstanceProperties(data.get(YamlConstants.INSTANCE_PROPERTIES) != null ? YamlSupport.getInstancePropertiesFromYamlContent(data) : Collections.emptyList());
        deploymentInstance.setComponentInstances(data.get(YamlConstants.COMPONENT_INSTANCES) != null ? YamlSupport.getComponentInstancesFromYamlContent(data) : Collections.emptyList());

        return deploymentInstance;
    }
}

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
import io.github.edmm.util.CastUtil;

import org.yaml.snakeyaml.Yaml;

public class YamlParser {

    private Map<String, Object> yamlData;
    private final DeploymentInstance deploymentInstance = new DeploymentInstance();

    public DeploymentInstance parseYamlAndTransformToDeploymentInstance(String fileInput) {

        parseYaml(fileInput);
        transformToDeploymentInstance();

        return this.deploymentInstance;
    }

    private void parseYaml(String fileInput) {
        Yaml yaml = new Yaml();
        InputStream input = null;
        try {
            input = new FileInputStream(new File(fileInput));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.yamlData = yaml.load(input);
    }

    private void transformToDeploymentInstance() {
        this.deploymentInstance.setName(this.yamlData.get(YamlConstants.NAME) != null ? String.valueOf(this.yamlData.get(YamlConstants.NAME)) : null);
        this.deploymentInstance.setVersion(this.yamlData.get(YamlConstants.VERSION) != null ? String.valueOf(this.yamlData.get(YamlConstants.VERSION)) : null);
        this.deploymentInstance.setState(this.yamlData.get(YamlConstants.STATE) != null ? InstanceState.InstanceStateForDeploymentInstance.valueOf(String.valueOf(this.yamlData.get(YamlConstants.STATE))) : null);
        this.deploymentInstance.setId(this.yamlData.get(YamlConstants.ID) != null ? String.valueOf(this.yamlData.get(YamlConstants.ID)) : null);
        this.deploymentInstance.setCreatedAt(this.yamlData.get(YamlConstants.CREATED_AT) != null ? String.valueOf(this.yamlData.get(YamlConstants.CREATED_AT)) : null);
        this.deploymentInstance.setDescription(this.yamlData.get(YamlConstants.DESCRIPTION) != null ? String.valueOf(this.yamlData.get(YamlConstants.DESCRIPTION)) : null);
        this.deploymentInstance.setMetadata(this.yamlData.get(YamlConstants.METADATA) != null ? Metadata.of(CastUtil.safelyCastToStringObjectMap(this.yamlData.get(YamlConstants.METADATA))) : Metadata.of(Collections.emptyMap()));
        this.deploymentInstance.setInstanceProperties(this.yamlData.get(YamlConstants.INSTANCE_PROPERTIES) != null ? YamlSupport.getInstancePropertiesFromYamlContent(this.yamlData) : Collections.emptyList());
        this.deploymentInstance.setComponentInstances(this.yamlData.get(YamlConstants.COMPONENT_INSTANCES) != null ? YamlSupport.getComponentInstancesFromYamlContent(this.yamlData) : Collections.emptyList());
    }
}

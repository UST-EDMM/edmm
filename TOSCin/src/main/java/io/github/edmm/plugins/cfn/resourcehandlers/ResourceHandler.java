package io.github.edmm.plugins.cfn.resourcehandlers;

import java.util.Map;

import com.amazonaws.services.cloudformation.model.StackResourceDetail;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

public interface ResourceHandler {
    boolean canHandleResource(String resourceType);

    void addResourceToTemplate(TServiceTemplate serviceTemplate, StackResourceDetail resource);

    default void populateNodeTemplateProperties(TNodeTemplate nodeTemplate, Map<String, String> additionalProperties) {
        if (nodeTemplate.getProperties() != null && nodeTemplate.getProperties().getKVProperties() != null) {
            nodeTemplate.getProperties()
                .getKVProperties()
                .entrySet()
                .stream()
                .filter(entry -> !additionalProperties.containsKey(entry.getKey()) || additionalProperties.get(entry.getKey())
                    .isEmpty())
                .forEach(entry -> additionalProperties.put(entry.getKey(),
                    entry.getValue() != null && !entry.getValue()
                        .isEmpty() ? entry.getValue() : "get_input: " + entry.getKey() + "_" + nodeTemplate.getId()
                        .replaceAll("(\\s)|(:)|(\\.)", "_")));
        }

        // workaround to set new properties
        nodeTemplate.setProperties(new TEntityTemplate.Properties());
        nodeTemplate.getProperties().setKVProperties(additionalProperties);
    }
}

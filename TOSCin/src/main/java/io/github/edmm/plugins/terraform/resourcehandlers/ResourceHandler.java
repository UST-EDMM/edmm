package io.github.edmm.plugins.terraform.resourcehandlers;

import java.util.Map;

import org.eclipse.winery.model.tosca.TServiceTemplate;

public interface ResourceHandler {
    boolean canHandleResource(Map<String, Object> resource);

    void addResourceToTemplate(TServiceTemplate serviceTemplate, Map<String, Object> resource);
}

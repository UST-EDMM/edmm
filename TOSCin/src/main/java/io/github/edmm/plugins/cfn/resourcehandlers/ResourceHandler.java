package io.github.edmm.plugins.cfn.resourcehandlers;

import com.amazonaws.services.cloudformation.model.StackResourceDetail;
import org.eclipse.winery.model.tosca.TServiceTemplate;

public interface ResourceHandler {
    boolean canHandleResource(String resourceType);

    void addResourceToTemplate(TServiceTemplate serviceTemplate, StackResourceDetail resource);
}

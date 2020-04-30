package io.github.edmm.plugins.cfn.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.amazonaws.services.cloudformation.model.StackResourceDetail;
import io.github.edmm.model.Metadata;
import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.plugins.cfn.model.Status;

public class CfnStackResourcesHandler {
    List<StackResourceDetail> stackResources;
    List<ComponentInstance> componentInstances = new ArrayList<>();

    public CfnStackResourcesHandler(List<StackResourceDetail> stackResources) {
        this.stackResources = stackResources;
    }

    public List<ComponentInstance> getComponentInstances() {
        this.stackResources.forEach(stackResource -> {
            ComponentInstance componentInstance = new ComponentInstance();
            componentInstance.setName(stackResource.getLogicalResourceId());
            componentInstance.setId(stackResource.getPhysicalResourceId());
            componentInstance.setDescription(stackResource.getDescription());
            componentInstance.setType(stackResource.getResourceType());
            componentInstance.setCreatedAt(String.valueOf(stackResource.getLastUpdatedTimestamp()));
            componentInstance.setState(Status.CfnStackResourceStatus.valueOf(stackResource.getResourceStatus()).toEDiMMComponentInstanceState());
            componentInstance.setMetadata(Metadata.of(Collections.emptyMap()));
            componentInstance.setInstanceProperties(Collections.emptyList());
            this.componentInstances.add(componentInstance);
        });
        return this.componentInstances;
    }
}

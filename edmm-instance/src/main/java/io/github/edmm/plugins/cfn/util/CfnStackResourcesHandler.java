package io.github.edmm.plugins.cfn.util;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.plugins.cfn.model.Status;

import com.amazonaws.services.cloudformation.model.StackResourceDetail;

public class CfnStackResourcesHandler {
    private List<StackResourceDetail> stackResources;
    private List<ComponentInstance> componentInstances = new ArrayList<>();

    public CfnStackResourcesHandler(List<StackResourceDetail> stackResources) {
        this.stackResources = stackResources;
    }

    public List<ComponentInstance> getComponentInstances() {
        this.stackResources.forEach(stackResource -> {
            // TODO: artifacts, operations, relations, instance props
            ComponentInstance componentInstance = new ComponentInstance();
            componentInstance.setName(stackResource.getLogicalResourceId());
            componentInstance.setId(stackResource.getPhysicalResourceId());
            componentInstance.setDescription(stackResource.getDescription());
            componentInstance.setType(stackResource.getResourceType());
            componentInstance.setCreatedAt(String.valueOf(stackResource.getLastUpdatedTimestamp()));
            componentInstance.setMetadata(new CfnMetadataHandler(stackResource).getMetadataOfComponentInstance());
            componentInstance.setState(Status.CfnStackResourceStatus.valueOf(stackResource.getResourceStatus()).toEDiMMComponentInstanceState());
            this.componentInstances.add(componentInstance);
        });
        return this.componentInstances;
    }
}

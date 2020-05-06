package io.github.edmm.plugins.cfn.util;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.plugins.cfn.model.Status;
import io.github.edmm.plugins.cfn.model.Template;

import com.amazonaws.services.cloudformation.model.StackResourceDetail;

public class CfnStackResourcesHandler {
    private final List<StackResourceDetail> stackResources;
    private final Template template;
    private final List<ComponentInstance> componentInstances = new ArrayList<>();

    public CfnStackResourcesHandler(List<StackResourceDetail> stackResources, Template template) {
        this.stackResources = stackResources;
        this.template = template;
    }

    public List<ComponentInstance> getComponentInstances() {
        this.stackResources.forEach(stackResource -> {
            // TODO artifacts
            ComponentInstance componentInstance = new ComponentInstance();
            componentInstance.setName(stackResource.getLogicalResourceId());
            componentInstance.setId(stackResource.getPhysicalResourceId());
            componentInstance.setDescription(stackResource.getDescription());
            componentInstance.setType(stackResource.getResourceType());
            componentInstance.setCreatedAt(String.valueOf(stackResource.getLastUpdatedTimestamp()));
            componentInstance.setMetadata(new CfnMetadataHandler(stackResource).getMetadataOfComponentInstance());
            componentInstance.setState(Status.CfnStackResourceStatus.valueOf(stackResource.getResourceStatus()).toEDiMMComponentInstanceState());
            componentInstance.setRelationInstances(new CfnStackRelationHandler(stackResource, this.stackResources, this.template).getRelationInstances());
            this.componentInstances.add(componentInstance);
        });
        return this.componentInstances;
    }
}

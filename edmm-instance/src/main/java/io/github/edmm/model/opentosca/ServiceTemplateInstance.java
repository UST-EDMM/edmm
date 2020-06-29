package io.github.edmm.model.opentosca;

import java.util.List;

import javax.xml.namespace.QName;

import io.github.edmm.model.edimm.DeploymentInstance;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ServiceTemplateInstance {
    QName serviceTemplateId;
    String serviceTemplateInstanceId;
    String createdAt;
    String csarId;
    TOSCAState.ServiceTemplateInstanceState state;
    List<NodeTemplateInstance> nodeTemplateInstances;
    List<RelationshipTemplateInstance> relationshipTemplateInstances;

    public static ServiceTemplateInstance ofDeploymentInstance(DeploymentInstance deploymentInstance) {
        ServiceTemplateInstance serviceTemplateInstance = new ServiceTemplateInstance();

        serviceTemplateInstance.setServiceTemplateInstanceId(deploymentInstance.getId());
        serviceTemplateInstance.setCreatedAt(deploymentInstance.getCreatedAt());
        serviceTemplateInstance.setCsarId(deploymentInstance.getName());
        serviceTemplateInstance.setServiceTemplateId(new QName(OpenTOSCANamespaces.OPENTOSCA_SERVICE_TEMPL, deploymentInstance.getName()));
        serviceTemplateInstance.setState(deploymentInstance.getState().toTOSCAServiceTemplateInstanceState());

        return serviceTemplateInstance;
    }
}

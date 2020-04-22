package io.github.edmm.core.transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.opentosca.OpenTOSCANamespaces;
import io.github.edmm.model.opentosca.NodeTemplateInstance;
import io.github.edmm.model.opentosca.RelationshipTemplateInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class TOSCATransformer {

    /**
     * Transform EDiMM to OpenTOSCA.
     *
     * @param deploymentInstance EDiMM deployment instance object
     * @return transformed service template instance
     */
    public static ServiceTemplateInstance transformEDiMMToOpenTOSCA(DeploymentInstance deploymentInstance) {
        ServiceTemplateInstance serviceTemplateInstance = new ServiceTemplateInstance();

        serviceTemplateInstance.setServiceTemplateInstanceId(deploymentInstance.getId());
        serviceTemplateInstance.setCreatedAt(deploymentInstance.getCreatedAt());
        serviceTemplateInstance.setCsarId(deploymentInstance.getName());
        serviceTemplateInstance.setServiceTemplateId(new QName(OpenTOSCANamespaces.OPENTOSCA_SERVICE_TEMPL_NAMESPACE, deploymentInstance.getName()));
        serviceTemplateInstance.setState(deploymentInstance.getState().toTOSCAServiceTemplateInstanceState());
        serviceTemplateInstance.setNodeTemplateInstances(createNodeTemplateInstances(deploymentInstance.getName(), deploymentInstance.getId(), deploymentInstance.getComponentInstances()));
        createRelationshipTemplateInstances(deploymentInstance.getId(), serviceTemplateInstance.getNodeTemplateInstances(), deploymentInstance.getComponentInstances());
        return serviceTemplateInstance;
    }

    /**
     * Transform EDiMM relation instances to OpenTOSCA relationship template instances.
     *
     * @param deploymentInstanceId  id of deployment instance
     * @param nodeTemplateInstances list of node template instances we want to retrieve relationship template instances
     *                              for
     * @param componentInstances    list of component instances we want to retrieve relationship template instances
     *                              from
     */
    private static void createRelationshipTemplateInstances(String deploymentInstanceId, List<NodeTemplateInstance> nodeTemplateInstances, List<ComponentInstance> componentInstances) {

        emptyIfNull(componentInstances).forEach(componentInstance -> {
            if (componentInstance.getRelationInstances() != null) {
                componentInstance.getRelationInstances().forEach(relationInstance -> {
                    RelationshipTemplateInstance relationshipTemplateInstance = new RelationshipTemplateInstance();
                    relationshipTemplateInstance.setServiceTemplateInstanceId(deploymentInstanceId);
                    relationshipTemplateInstance.setRelationshipType(new QName(OpenTOSCANamespaces.OPENTOSCA_REL_TYPE_NAMESPACE, relationInstance.getType()));
                    relationshipTemplateInstance.setSourceNodeTemplateInstanceId(componentInstance.getId());
                    relationshipTemplateInstance.setTargetNodeTemplateInstanceId(relationInstance.getTargetInstanceId());
                    relationshipTemplateInstance.setRelationshipTemplateId(new QName(OpenTOSCANamespaces.OPENTOSCA_REL_TEMPL_NAMESPACE, relationInstance.getId()));
                    relationshipTemplateInstance.setState(componentInstance.getState().toTOSCANodeTemplateInstanceState().convertToRelationshipTemplateInstanceState());
                    relationshipTemplateInstance.setInstanceProperties(emptyIfNull(relationInstance.getInstanceProperties())
                        .stream().map(InstanceProperty::convertToTOSCAProperty).collect(Collectors.toList()));

                    nodeTemplateInstances.stream().filter(x -> x.getNodeTemplateInstanceId().equals(componentInstance.getId())).findFirst().get().addToOutgoingRelationshipTemplateInstances(relationshipTemplateInstance);
                    nodeTemplateInstances.stream().filter(x -> x.getNodeTemplateInstanceId().equals(relationInstance.getTargetInstanceId())).findFirst().get().addToIngoingRelationshipTemplateInstances(relationshipTemplateInstance);
                });
            }
        });
    }

    /**
     * Transform EDiMM component instances to OpenTOSCA node template instances.
     *
     * @param deploymentInstanceName name of deployment instance
     * @param deploymentInstanceId   id of deployment instance
     * @param componentInstances     component instances of deployment instances to be transformed
     * @return list of transformed node template instances
     */
    private static List<NodeTemplateInstance> createNodeTemplateInstances(String deploymentInstanceName, String deploymentInstanceId, List<ComponentInstance> componentInstances) {
        List<NodeTemplateInstance> nodeTemplateInstances = new ArrayList<>();
        emptyIfNull(componentInstances).forEach(componentInstance -> {
            NodeTemplateInstance nodeTemplateInstance = new NodeTemplateInstance();

            nodeTemplateInstance.setNodeTemplateInstanceId(componentInstance.getId());
            nodeTemplateInstance.setNodeType(new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TYPE_NAMESPACE, componentInstance.getType()));
            nodeTemplateInstance.setNodeTemplateId(new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TEMPL_NAMESPACE, componentInstance.getName()));
            nodeTemplateInstance.setServiceTemplateInstanceId(deploymentInstanceId);
            nodeTemplateInstance.setServiceTemplateId(new QName(OpenTOSCANamespaces.OPENTOSCA_SERVICE_TEMPL_NAMESPACE, deploymentInstanceName));
            nodeTemplateInstance.setState(componentInstance.getState().toTOSCANodeTemplateInstanceState());
            nodeTemplateInstance.setInstanceProperties(emptyIfNull(componentInstance.getInstanceProperties())
                .stream().map(InstanceProperty::convertToTOSCAProperty).collect(Collectors.toList()));
            nodeTemplateInstances.add(nodeTemplateInstance);
        });
        return nodeTemplateInstances;
    }
}

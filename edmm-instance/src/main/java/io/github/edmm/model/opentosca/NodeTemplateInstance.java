package io.github.edmm.model.opentosca;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import io.github.edmm.core.transformation.TOSCATypeMapperImplementation;
import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.InstanceProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Setter
@Getter
@ToString
public class NodeTemplateInstance {
    String nodeTemplateInstanceId;
    String serviceTemplateInstanceId;
    QName nodeTemplateId;
    QName nodeType;
    TOSCAState.NodeTemplateInstanceState state;
    QName serviceTemplateId;
    List<TOSCAProperty> instanceProperties;

    public static NodeTemplateInstance ofComponentInstance(String deploymentInstanceId, String deploymentInstanceName, ComponentInstance componentInstance) {
        NodeTemplateInstance nodeTemplateInstance = new NodeTemplateInstance();

        nodeTemplateInstance.setNodeTemplateInstanceId(componentInstance.getId());
        nodeTemplateInstance.setNodeTemplateId(new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TEMPL, componentInstance.getName()));
        nodeTemplateInstance.setNodeType(tryNodeTypeRefinement(componentInstance.getType().toTOSCABaseNodeType(), componentInstance.getInstanceProperties()));
        nodeTemplateInstance.setServiceTemplateInstanceId(deploymentInstanceId);
        nodeTemplateInstance.setServiceTemplateId(new QName(OpenTOSCANamespaces.OPENTOSCA_SERVICE_TEMPL, deploymentInstanceName));
        nodeTemplateInstance.setState(componentInstance.getState().toTOSCANodeTemplateInstanceState());
        nodeTemplateInstance.setInstanceProperties(emptyIfNull(componentInstance.getInstanceProperties())
            .stream().map(InstanceProperty::convertToTOSCAProperty).collect(Collectors.toList()));
        if (componentInstance.getType().toTOSCABaseNodeType().equals(TOSCABaseTypes.TOSCABaseNodeTypes.Compute)) {
            nodeTemplateInstance.getInstanceProperties().add(InstanceProperty.convertToTOSCAProperty(new InstanceProperty("State", String.class.getSimpleName(), "Running")));
        }
        return nodeTemplateInstance;
    }

    private static QName tryNodeTypeRefinement(TOSCABaseTypes.TOSCABaseNodeTypes toscaBaseNodeType, List<InstanceProperty> instanceProperties) {
        QName normativeNodeType = new QName(OpenTOSCANamespaces.OPENTOSCA_NORMATIVE_NODE_TYPE, String.valueOf(toscaBaseNodeType));
        TOSCATypeMapperImplementation toscaRefiner = new TOSCATypeMapperImplementation();
        QName refinedNodeType = toscaRefiner.refineTOSCAType(normativeNodeType, instanceProperties);

        return refinedNodeType != null ? refinedNodeType : normativeNodeType;
    }
}

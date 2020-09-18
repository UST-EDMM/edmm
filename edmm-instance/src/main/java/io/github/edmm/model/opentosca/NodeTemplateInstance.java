package io.github.edmm.model.opentosca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import io.github.edmm.core.transformation.TOSCATypeMapperImplementation;
import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.ComponentType;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.edimm.PropertyKey;
import io.github.edmm.util.Constants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
        nodeTemplateInstance.setInstanceProperties(handlePropertyKeyMapping(componentInstance.getInstanceProperties(), componentInstance.getType()));
        nodeTemplateInstance.getInstanceProperties().add(InstanceProperty.convertToTOSCAProperty(new InstanceProperty(Constants.STATE, String.class.getSimpleName(), Constants.RUNNING)));
        return nodeTemplateInstance;
    }

    private static QName tryNodeTypeRefinement(TOSCABaseTypes.TOSCABaseNodeTypes toscaBaseNodeType, List<InstanceProperty> instanceProperties) {
        QName normativeNodeType = new QName(OpenTOSCANamespaces.OPENTOSCA_NORMATIVE_NODE_TYPE, String.valueOf(toscaBaseNodeType));
        TOSCATypeMapperImplementation toscaRefiner = new TOSCATypeMapperImplementation();
        QName refinedNodeType = toscaRefiner.refineTOSCAType(normativeNodeType, instanceProperties);

        return refinedNodeType != null ? refinedNodeType : normativeNodeType;
    }

    private static List<TOSCAProperty> handlePropertyKeyMapping(List<InstanceProperty> instanceProperties, ComponentType unrefinedType) {
        List<TOSCAProperty> toscaProperties = new ArrayList<>();
        for (InstanceProperty property : instanceProperties) {
            String mappedKey = getTOSCAPropertyMapping(property.getKey(), unrefinedType);
            if (mappedKey != null) {
                String normativeKey = property.getKey();
                property.setKey(mappedKey);
                toscaProperties.add(InstanceProperty.convertToTOSCAProperty(property));
                property.setKey(normativeKey);
            }
        }
        return toscaProperties;
    }

    private static String getTOSCAPropertyMapping(String originalPropertyName, ComponentType unrefinedType) {
        switch (unrefinedType) {
            case Auth0:
                return PropertyKey.Auth0.valueOf(originalPropertyName).toTOSCAPropertyKey();
            case Compute:
                return PropertyKey.Compute.valueOf(originalPropertyName).toTOSCAPropertyKey();
            case Database:
                return PropertyKey.Database.valueOf(originalPropertyName).toTOSCAPropertyKey();
            case DBaaS:
                return PropertyKey.Dbaas.valueOf(originalPropertyName).toTOSCAPropertyKey();
            case DBMS:
                return PropertyKey.Dbms.valueOf(originalPropertyName).toTOSCAPropertyKey();
            case MySQL_Database:
                return PropertyKey.MysqlDatabase.valueOf(originalPropertyName).toTOSCAPropertyKey();
            case MySQL_DBMS:
                return PropertyKey.MysqlDbms.valueOf(originalPropertyName).toTOSCAPropertyKey();
            case PaaS:
                return PropertyKey.Paas.valueOf(originalPropertyName).toTOSCAPropertyKey();
            case Platform:
                return PropertyKey.Platform.valueOf(originalPropertyName).toTOSCAPropertyKey();
            case SaaS:
                return PropertyKey.Saas.valueOf(originalPropertyName).toTOSCAPropertyKey();
            case Software_Component:
                return PropertyKey.SoftwareComponent.valueOf(originalPropertyName).toTOSCAPropertyKey();
            case Tomcat:
                return PropertyKey.Tomcat.valueOf(originalPropertyName).toTOSCAPropertyKey();
            case Web_Application:
                return PropertyKey.WebApplication.valueOf(originalPropertyName).toTOSCAPropertyKey();
            case Web_Server:
                return PropertyKey.WebServer.valueOf(originalPropertyName).toTOSCAPropertyKey();
            default:
                return null;
        }
    }
}

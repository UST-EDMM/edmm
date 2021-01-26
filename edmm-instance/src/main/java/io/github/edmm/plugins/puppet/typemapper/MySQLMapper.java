package io.github.edmm.plugins.puppet.typemapper;

import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

import io.github.edmm.core.transformation.TypeTransformer;
import io.github.edmm.exporter.WineryConnector;

public class MySQLMapper implements TypeTransformer {

    @Override
    public boolean canHandle(String component, String version) {
        return component.equalsIgnoreCase("MySQL::Server")
            || component.equalsIgnoreCase("MySQL::Client")
            || component.equalsIgnoreCase("MySQL::DBMS")
            || component.equalsIgnoreCase("MySQL::DB")
            || component.equalsIgnoreCase("MySQL")
            || component.equalsIgnoreCase("DB");
    }

    @Override
    public QName performTransformation(String component, String version) {
        String[] type = {"MySQL-DBMS"};

        if (component.equalsIgnoreCase("MySQL::DB")
            || component.equalsIgnoreCase("DB")) {
            type[0] = "MySQL-DB";
        } else if (component.equalsIgnoreCase("MySQL::CLient")) {
            return null;
        }

        return WineryConnector.getInstance().getBaseNodeTypesQNames().stream()
            .filter(qName -> VersionUtils.getNameWithoutVersion(qName.getLocalPart()).equalsIgnoreCase(type[0]))
            .min((o1, o2) -> {
                int compareTo = o1.getNamespaceURI().compareTo(o2.getNamespaceURI());
                if (compareTo == 0) {
                    return o1.getLocalPart().compareTo(o2.getLocalPart());
                }
                return compareTo;
            })
            .orElse(null);
    }

    @Override
    public boolean refineHost(TNodeTemplate nodeTemplate, TNodeTemplate defaultHost, TTopologyTemplate topologyTemplate) {
        if (nodeTemplate.getType().getLocalPart().toLowerCase().startsWith("MySQL-DB".toLowerCase())) {
            if (nodeTemplate.getType().getLocalPart().toLowerCase().startsWith("MySQL-DBMS".toLowerCase())) {
                Optional<TNodeTemplate> optionalDB = topologyTemplate.getNodeTemplates().stream()
                    .filter(node -> node.getType().getLocalPart().toLowerCase().startsWith("MySQl-DB".toLowerCase()))
                    .filter(node -> ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, node).stream()
                        .noneMatch(rel -> rel.getTargetElement().getRef().getType().getLocalPart().toLowerCase()
                            .startsWith("MySQL-DBMS".toLowerCase())
                        )
                    ).findFirst();
                if (optionalDB.isPresent()) {
                    ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, optionalDB.get()).stream()
                        .filter(rel -> rel.getType().equals(ToscaBaseTypes.hostedOnRelationshipType))
                        .forEach(rel -> topologyTemplate.getNodeTemplateOrRelationshipTemplate().remove(rel));
                    ModelUtilities.createRelationshipTemplateAndAddToTopology(optionalDB.get(), nodeTemplate,
                        ToscaBaseTypes.hostedOnRelationshipType, topologyTemplate);
                }
            } else {
                Optional<TNodeTemplate> optionalDbms = topologyTemplate.getNodeTemplates().stream()
                    .filter(node -> node.getType().getLocalPart().toLowerCase().startsWith("MySQl-DBMS".toLowerCase())
                        || node.getType().getLocalPart().toLowerCase().startsWith("DBMS".toLowerCase()))
                    .filter(node -> ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, node).stream()
                        .anyMatch(relationship -> relationship.getTargetElement().getRef().equals(defaultHost))
                    ).findFirst();
                if (optionalDbms.isPresent()) {
                    ModelUtilities.createRelationshipTemplateAndAddToTopology(nodeTemplate, optionalDbms.get(),
                        ToscaBaseTypes.hostedOnRelationshipType, topologyTemplate);
                    return true;
                }
            }
        }

        return false;
    }
}

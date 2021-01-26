package io.github.edmm.plugins.puppet.typemapper;

import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import io.github.edmm.core.transformation.TypeTransformer;
import io.github.edmm.exporter.WineryConnector;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

public class WebApplicationMapper implements TypeTransformer {

    public static QName toscaWebServer = QName.valueOf("{http://docs.oasis-open.org/tosca/ToscaNormativeTypes/nodetypes}WebServer");
    public static QName toscaWebApp = QName.valueOf("{http://docs.oasis-open.org/tosca/ToscaNormativeTypes/nodetypes}WebApplication");

    @Override
    public boolean canHandle(String component, String version) {
        return false;
    }

    @Override
    public QName performTransformation(String component, String version) {
        return null;
    }

    @Override
    public boolean refineHost(TNodeTemplate webApp, TNodeTemplate defaultHost, TTopologyTemplate topologyTemplate) {
        Map<QName, TNodeType> nodeTypes = WineryConnector.getInstance().getNodeTypes();

        if (ModelUtilities.isOfType(toscaWebApp, webApp.getType(), nodeTypes)) {
            Optional<TNodeTemplate> server = topologyTemplate.getNodeTemplates().stream()
                .filter(webServer -> ModelUtilities.isOfType(toscaWebServer, webServer.getType(), nodeTypes))
                .findFirst();
            if (server.isPresent()) {
                TNodeTemplate webServer = server.get();
                ModelUtilities.createRelationshipTemplateAndAddToTopology(
                    webApp, webServer, ToscaBaseTypes.hostedOnRelationshipType, topologyTemplate);
                return true;
            }
        }

        return false;
    }
}

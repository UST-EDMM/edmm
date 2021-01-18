package io.github.edmm.plugins.puppet.typemapper;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import io.github.edmm.core.transformation.TypeTransformer;
import io.github.edmm.exporter.WineryConnector;

public class TomcatMapper implements TypeTransformer {

    @Override
    public boolean canHandle(String component, String version) {
        return component.toLowerCase().contains("tomcat");
    }

    @Override
    public QName performTransformation(String component, String version) {
        return WineryConnector.getInstance().getBaseNodeTypesQNames().stream()
            .filter(qName -> qName.getLocalPart().startsWith("Tomcat"))
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
        return false;
    }
}

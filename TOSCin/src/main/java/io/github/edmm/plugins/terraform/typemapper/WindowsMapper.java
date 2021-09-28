package io.github.edmm.plugins.terraform.typemapper;

import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import io.github.edmm.core.transformation.TypeTransformer;
import io.github.edmm.exporter.WineryConnector;

public class WindowsMapper implements TypeTransformer {

    private final WineryConnector wineryConnector;

    public WindowsMapper(WineryConnector wineryConnector) {
        this.wineryConnector = wineryConnector;
    }

    @Override
    public boolean canHandle(String component, String version) {
        return component.startsWith("windows") && Objects.equals(version, "10.0");
    }

    @Override
    public QName performTransformation(String component, String version) {
        return wineryConnector.getBaseNodeTypesQNames()
            .stream()
            .filter(aQName -> Objects.equals(VersionUtils.getNameWithoutVersion(aQName.getLocalPart()), "Windows"))
            .filter(aQName -> {
                String nodeTypeOsVersion = VersionUtils.getVersion(aQName.getLocalPart()).getComponentVersion();
                return Objects.equals(nodeTypeOsVersion, version);
            })
            .findFirst()
            .orElse(null);
    }

    @Override
    public boolean refineHost(
        TNodeTemplate nodeTemplate, TNodeTemplate defaultHost, TTopologyTemplate topologyTemplate) {
        return false;
    }
}

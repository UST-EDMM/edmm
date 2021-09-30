package io.github.edmm.plugins.kubernetes.typemapper;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import io.github.edmm.core.transformation.TypeTransformer;
import io.github.edmm.exporter.WineryConnector;

import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

public class UbuntuMapper implements TypeTransformer {
    public static final String UBUNTU_NODE_TYPE_NAME = "Ubuntu";
    private static final Pattern UBUNTU_COMPONENT_PATTERN = Pattern.compile("Ubuntu (\\d*.\\d*.\\d*).*");
    private static final Pattern UBUNTU_NODE_TYPE_OS_VERSION_PATTERN = Pattern.compile("Ubuntu_(\\d*.\\d*.\\d*).*");
    private final WineryConnector myWineryConnector;

    public UbuntuMapper(WineryConnector aWineryConnector) {
        myWineryConnector = aWineryConnector;
    }

    @Override
    public boolean canHandle(String component, String version) {
        return UBUNTU_COMPONENT_PATTERN.matcher(component).matches();
    }

    @Override
    public QName performTransformation(String component, String version) {
        Matcher matcher = UBUNTU_COMPONENT_PATTERN.matcher(component);
        if (matcher.matches()) {
            String componentOsVersion = matcher.group(1);
            return myWineryConnector.getBaseNodeTypesQNames()
                    .stream()
                    .filter(aQName -> Objects.equals(VersionUtils.getNameWithoutVersion(aQName.getLocalPart()),
                            UBUNTU_NODE_TYPE_NAME))
                    .filter(aQName -> {
                        String nodeTypeOsVersion = VersionUtils.getVersion(aQName.getLocalPart()).getComponentVersion();
                        return Objects.equals(nodeTypeOsVersion, componentOsVersion);
                    })
                    .findFirst()
                    .orElse(null);
        } else {
            return null;
        }
    }

    @Override
    public boolean refineHost(
            TNodeTemplate nodeTemplate,
            TNodeTemplate defaultHost,
            TTopologyTemplate topologyTemplate) {
        return false;
    }
}

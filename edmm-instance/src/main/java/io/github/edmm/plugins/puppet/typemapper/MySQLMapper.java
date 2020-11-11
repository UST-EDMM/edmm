package io.github.edmm.plugins.puppet.typemapper;

import javax.xml.namespace.QName;

import io.github.edmm.core.transformation.TypeTransformer;
import io.github.edmm.exporter.WineryConnector;

import org.eclipse.winery.common.version.VersionUtils;

public class MySQLMapper implements TypeTransformer {

    @Override
    public boolean canHandle(String component, String version) {
        return component.toLowerCase().equals("MySQL::Server".toLowerCase())
            || component.toLowerCase().equals("MySQL::DBMS".toLowerCase())
            || component.toLowerCase().equals("MySQL::DB".toLowerCase())
            || component.toLowerCase().equals("MySQL".toLowerCase());
    }

    @Override
    public QName performTransformation(String component, String version) {
        String[] type = {"MySQL-DBMS"};

        if (component.toLowerCase().equals("MySQL::DB".toLowerCase())) {
            type[0] = "MySQL-DB";
        }

        return WineryConnector.getInstance().getBaseNodeTypesQNames().stream()
            .filter(qName -> VersionUtils.getNameWithoutVersion(qName.getLocalPart()).toLowerCase().equals(type[0].toLowerCase()))
            .findFirst()
            .orElse(null);
    }
}

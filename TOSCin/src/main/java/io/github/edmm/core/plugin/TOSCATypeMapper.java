package io.github.edmm.core.plugin;

import java.util.List;

import javax.xml.namespace.QName;

import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.opentosca.OpenTOSCANamespaces;

public interface TOSCATypeMapper {

    // TODO: replace this with actual repository search, currently this is hardcoded for demo purposes
    static QName searchWineryRepositoryForType(String type) {
        type = type.replaceAll("\\s+", "");
        switch (type) {
            case "Ubuntu18.04":
                return new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TYPE, "Ubuntu-VM_18.04-w1");
            case "Ubuntu16.04":
                return new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TYPE, "Ubuntu-VM_16.04-w1");
            case "Ubuntu14.04":
                return new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TYPE, "Ubuntu-VM_14.04-w1");
            case "mysql-server":
                return new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TYPE, "MySQL-DBMS_5.7-w1");
            case "docker":
                return new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TYPE, "DockerEngine");
            case "tomcat8":
            case "tomcat":
                return new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TYPE, "Tomcat_8-w1");
            case "mongodb":
                return new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TYPE, "MongoDB-Server_3.2-w1");
            case "java":
                return new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TYPE, "Java_8-w1");
            case "JavaApp":
                return new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TYPE, "SpringWebApp_w1");
            case "OpenStackCompute":
                return new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TYPE, "OpenStack_22-Victoria-w1");
            case "nginx":
                return new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TYPE, "NGINX_latest-w1");
            default:
                return null;
        }
    }

    QName refineTOSCAType(QName qName, List<InstanceProperty> instanceProperties);
}

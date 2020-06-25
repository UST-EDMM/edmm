package io.github.edmm.core.plugin;

import java.util.List;

import javax.xml.namespace.QName;

import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.opentosca.OpenTOSCANamespaces;

public interface TOSCATypeMapper {
    QName refineTOSCAType(QName qName, List<InstanceProperty> instanceProperties);

    // TODO: replace this with actual repository search that returns derived qname
    static QName searchWineryRepositoryForType(String type) {
        if (type.equals("Ubuntu18.04")) {
            return new QName(OpenTOSCANamespaces.OPENTOSCA_NODE_TYPE, "Ubuntu-VM_18.04-w1");
        }
        return null;
    }
}

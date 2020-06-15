package io.github.edmm.plugins.puppet.util;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.ComponentType;
import io.github.edmm.plugins.puppet.model.Master;

public class PuppetNodeHandler {
    public static List<ComponentInstance> getComponentInstances(Master master) {
        List<ComponentInstance> componentInstances = new ArrayList<>();
        master.getNodes().forEach(node -> {
            ComponentInstance componentInstance = new ComponentInstance();
            componentInstance.setId(String.valueOf(node.getCertname().hashCode()));
            // node is always of type compute
            componentInstance.setType(ComponentType.Compute);
            componentInstance.setName(node.getCertname());
            componentInstance.setInstanceProperties(PuppetPropertiesHandler.getComponentInstanceProperties(node.getFacts()));
            componentInstance.setState(node.getState().toEDIMMComponentInstanceState());
            componentInstance.setRelationInstances(PuppetRelationHandler.getRelationInstances(master, node));
            componentInstances.add(componentInstance);
        });

        return componentInstances;
    }
}

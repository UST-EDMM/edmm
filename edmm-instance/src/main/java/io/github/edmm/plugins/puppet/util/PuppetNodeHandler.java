package io.github.edmm.plugins.puppet.util;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.ComponentType;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.edimm.PropertyKey;
import io.github.edmm.plugins.puppet.model.Fact;
import io.github.edmm.plugins.puppet.model.FactType;
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
            componentInstance.getInstanceProperties().add(new InstanceProperty("original_type", String.class.getSimpleName(), getTypeFromFacts(node.getFacts())));
            componentInstance.setState(node.getState().toEDIMMComponentInstanceState());
            // we neglect relations for now
            componentInstances.add(componentInstance);
        });

        return componentInstances;
    }

    private static String getTypeFromFacts(List<Fact> facts) {
        Fact operatingSystemFact = facts.stream().filter(fact -> fact.getName().equals(FactType.OperatingSystem.toString().toLowerCase())).findFirst().orElse(null);
        Fact operatingSystemReleaseFact = facts.stream().filter(fact -> fact.getName().equals(FactType.OperatingSystemRelease.toString().toLowerCase())).findFirst().orElse(null);

        return operatingSystemFact.getValue() + operatingSystemReleaseFact.getValue();
    }
}

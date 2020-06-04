package io.github.edmm.plugins.puppet.util;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.plugins.puppet.model.Fact;
import io.github.edmm.plugins.puppet.model.FactType;
import io.github.edmm.plugins.puppet.model.Node;

public class PuppetNodeHandler {
    public static List<ComponentInstance> getComponentInstances(List<Node> nodes) {
        List<ComponentInstance> componentInstances = new ArrayList<>();
        nodes.forEach(node -> {
            ComponentInstance componentInstance = new ComponentInstance();
            componentInstance.setId(String.valueOf(node.getCertname().hashCode()));
            componentInstance.setType(getTypeFromFacts(node.getFacts()));
            componentInstance.setName(node.getCertname());
            componentInstance.setInstanceProperties(PuppetPropertiesHandler.getComponentInstanceProperties(node.getFacts()));
            componentInstance.setState(node.getState().toEDIMMComponentInstanceState());
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

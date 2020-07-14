package io.github.edmm.plugins.puppet.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.ComponentType;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.edimm.InstanceState;
import io.github.edmm.model.edimm.RelationInstance;
import io.github.edmm.model.edimm.RelationType;
import io.github.edmm.plugins.puppet.model.Fact;
import io.github.edmm.plugins.puppet.model.FactType;
import io.github.edmm.plugins.puppet.model.Master;
import io.github.edmm.plugins.puppet.model.Report;
import io.github.edmm.plugins.puppet.model.ResourceEventEntry;
import io.github.edmm.plugins.puppet.model.ResourceType;
import io.github.edmm.util.Constants;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
            componentInstance.getInstanceProperties().add(new InstanceProperty(Constants.TYPE, String.class.getSimpleName(), getTypeFromFacts(node.getFacts())));
            componentInstance.setState(node.getState().toEDIMMComponentInstanceState());
            // we neglect relations for now
            componentInstances.add(componentInstance);
            componentInstances.addAll(identifyPackagesOnPuppetNode(master, componentInstance, node.getCertname()));
        });


        return componentInstances;
    }

    private static List<ComponentInstance> identifyPackagesOnPuppetNode(Master master, ComponentInstance componentInstance, String certName) {
        List<Report> allReports = new Gson().fromJson(master.executeCommandAndHandleResult(Commands.GET_ALL_REPORTS), new TypeToken<List<Report>>() {
        }.getType());
        return identifyRelevantResourceEventEntries(allReports, componentInstance, certName);

    }

    private static String getTypeFromFacts(List<Fact> facts) {
        Fact operatingSystemFact = facts.stream().filter(fact -> fact.getName().equals(FactType.OperatingSystem.toString().toLowerCase())).findFirst().orElse(null);
        Fact operatingSystemReleaseFact = facts.stream().filter(fact -> fact.getName().equals(FactType.OperatingSystemRelease.toString().toLowerCase())).findFirst().orElse(null);

        return operatingSystemFact.getValue() + operatingSystemReleaseFact.getValue();
    }

    private static List<ComponentInstance> identifyRelevantResourceEventEntries(List<Report> allReports, ComponentInstance componentInstance, String certName) {
        List<ComponentInstance> componentInstances = new ArrayList<>();
        for (Report report : allReports) {
            if (report.getResource_events().getData() == null || !report.getCertname().equals(certName)) {
                continue;
            }
            for (ResourceEventEntry resourceEventEntry : report.getResource_events().getData()) {
                if (checkIfResourceEventEntryIsSuitable(resourceEventEntry)) {
                    componentInstances.add(generateComponentInstanceFromResourceEventEntry(resourceEventEntry, componentInstance, certName));
                }
            }
        }
        return componentInstances;
    }

    private static boolean checkIfResourceEventEntryIsSuitable(ResourceEventEntry resourceEventEntry) {
        return resourceEventEntry.getResource_type() != null && resourceEventEntry.getResource_type().equals(ResourceType.Package) && resourceEventEntry.getStatus() != null && resourceEventEntry.getStatus().equals("success");
    }

    private static ComponentInstance generateComponentInstanceFromResourceEventEntry(ResourceEventEntry entry, ComponentInstance componentInstance, String certName) {
        ComponentInstance packageComponent = new ComponentInstance();
        packageComponent.setId(String.valueOf(entry.getResource_title().hashCode()));
        packageComponent.setName(entry.getResource_title());
        packageComponent.setState(InstanceState.InstanceStateForComponentInstance.CREATED);
        packageComponent.setInstanceProperties(Collections.singletonList(new InstanceProperty("original_type", String.class.getSimpleName(), entry.getResource_title())));
        packageComponent.setType(getComponentTypeForPuppetPackage(entry.getResource_title()));
        RelationInstance relationInstance = new RelationInstance();
        relationInstance.setType(RelationType.HostedOn);
        relationInstance.setTargetInstance(componentInstance.getName());
        relationInstance.setId(entry.getResource_title() + String.valueOf(RelationType.HostedOn));
        packageComponent.setRelationInstances(Collections.singletonList(relationInstance));

        return packageComponent;
    }

    private static ComponentType getComponentTypeForPuppetPackage(String resourceTitle) {
        // this is bad, just hardcoded :/, maybe retrieve puppetlabs officially supported package list dynamically
        switch (resourceTitle) {
            case "mysql-server":
                return ComponentType.MySQL_DBMS;
            case "docker":
                return ComponentType.Platform;
            case "tomcat8":
                return ComponentType.Web_Server;
            case "mongodb":
                return ComponentType.DBMS;
            case "java":
                return ComponentType.Software_Component;
            default:
                return ComponentType.Software_Component;
        }

    }
}

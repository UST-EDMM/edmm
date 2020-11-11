package io.github.edmm.plugins.puppet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.ComponentType;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.edimm.InstanceState;
import io.github.edmm.model.edimm.PropertyKey;
import io.github.edmm.model.edimm.RelationInstance;
import io.github.edmm.model.edimm.RelationType;
import io.github.edmm.plugins.puppet.model.Fact;
import io.github.edmm.plugins.puppet.model.FactType;
import io.github.edmm.plugins.puppet.model.Master;
import io.github.edmm.plugins.puppet.model.PuppetResourceStatus;
import io.github.edmm.plugins.puppet.model.Report;
import io.github.edmm.plugins.puppet.model.ResourceEventEntry;
import io.github.edmm.plugins.puppet.model.ResourceType;
import io.github.edmm.util.Constants;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuppetNodeHandler {

    private static final Logger logger = LoggerFactory.getLogger(PuppetNodeHandler.class);

    public static List<ComponentInstance> getComponentInstances(Master master) {
        List<ComponentInstance> componentInstances = new ArrayList<>();
        master.getNodes().forEach(node -> {
            ComponentInstance componentInstance = new ComponentInstance();
            componentInstance.setId(UUID.randomUUID().toString());
            // node is always of type compute
            componentInstance.setType(ComponentType.Compute);
            componentInstance.setName(node.getCertname());
            componentInstance.setInstanceProperties(PuppetPropertiesHandler.getComponentInstanceProperties(componentInstance.getType(), node.getFacts()));
            componentInstance.getInstanceProperties().add(new InstanceProperty(Constants.TYPE, String.class.getSimpleName(), getTypeFromFacts(node.getFacts())));
            componentInstance.setState(node.getState().toEDIMMComponentInstanceState());
            ComponentInstance hyperVisor = createHypervisorComponentInstance(componentInstance);

            if (hyperVisor != null) {
                RelationInstance hostedOnHyperVisor = new RelationInstance();
                hostedOnHyperVisor.setId(UUID.randomUUID().toString());
                hostedOnHyperVisor.setType(RelationType.HostedOn);
                hostedOnHyperVisor.setTargetInstance(hyperVisor.getId());
                componentInstance.setRelationInstances(Collections.singletonList(hostedOnHyperVisor));
                // check if hypervisor already exists, only add to list if not
                if (componentInstances.stream().noneMatch(componentInstance1 -> componentInstance1.getId().equals(hyperVisor.getId()))) {
                    componentInstances.add(hyperVisor);
                }
            }

            componentInstances.add(componentInstance);
            componentInstances.addAll(identifyPackagesOnPuppetNode(master, componentInstance, node.getCertname()));
        });
        return componentInstances;
    }

    private static ComponentInstance createHypervisorComponentInstance(ComponentInstance inputComponentInstance) {
        String hyperVisorName = retrieveHypervisorName(inputComponentInstance);

        if (hyperVisorName != null) {
            ComponentInstance componentInstance = new ComponentInstance();
            componentInstance.setId(UUID.randomUUID().toString());
            componentInstance.setName(hyperVisorName.replace(" ", ""));
            componentInstance.setState(InstanceState.InstanceStateForComponentInstance.CREATED);
            InstanceProperty typeProp = new InstanceProperty(Constants.TYPE, String.class.getSimpleName(), hyperVisorName);
            componentInstance.setInstanceProperties(Collections.singletonList(typeProp));
            componentInstance.setType(ComponentType.Compute);

            return componentInstance;
        }

        return null;
    }

    public static List<List<ResourceEventEntry>> identifyRelevantReports(Master master, String certName) {
        List<Report> allReports = new Gson()
            .fromJson(master.executeCommandAndHandleResult(Commands.GET_ALL_REPORTS), new TypeToken<List<Report>>() { }.getType());
        return allReports.stream()
            .filter(report -> report.getResource_events().getData() != null && report.getCertname().equals(certName))
            .map(report -> report.getResource_events().getData())
            .collect(Collectors.toList());
    }

    public static List<ComponentInstance> identifyPackagesOnPuppetNode(Master master, ComponentInstance componentInstance, String certName) {
        List<Report> allReports = new Gson().fromJson(master.executeCommandAndHandleResult(Commands.GET_ALL_REPORTS), new TypeToken<List<Report>>() {
        }.getType());

        return identifyRelevantResourceEventEntries(allReports, componentInstance, certName);
    }

    private static String getTypeFromFacts(List<Fact> facts) {
        Fact operatingSystemFact = facts.stream().filter(fact -> fact.getName().equals(FactType.OperatingSystem.toString().toLowerCase())).findFirst().orElse(null);
        Fact operatingSystemReleaseFact = facts.stream().filter(fact -> fact.getName().equals(FactType.OperatingSystemRelease.toString().toLowerCase())).findFirst().orElse(null);

        return operatingSystemFact != null && operatingSystemReleaseFact != null
            ? operatingSystemFact.getValue() + operatingSystemReleaseFact.getValue().toString()
            : "";
    }

    private static List<ComponentInstance> identifyRelevantResourceEventEntries(List<Report> allReports, ComponentInstance componentInstance, String certName) {
        List<ComponentInstance> componentInstances = new ArrayList<>();
        List<List<ResourceEventEntry>> collect = allReports.stream()
            .filter(report -> report.getResource_events().getData() != null && report.getCertname().equals(certName))
            .map(report -> report.getResource_events().getData())
            .collect(Collectors.toList());

            collect.forEach(resourceEventEntries -> {
                for (ResourceEventEntry resourceEventEntry : resourceEventEntries) {
                    if (checkIfResourceEventEntryIsSuitable(resourceEventEntry)) {
                        ComponentInstance generatedComponentInstance = generateComponentInstanceFromResourceEventEntry(resourceEventEntry, componentInstance, certName);
                        logger.info("Identified component instance {}", generatedComponentInstance.getName());
                        if (generatedComponentInstance.getType().equals(ComponentType.Tomcat)) {
                            ComponentInstance webApp = searchForWebAppsInTomcatDirectory(componentInstance, generatedComponentInstance);
                            if (webApp != null) {
                                componentInstances.add(webApp);
                            }
                        }
                        componentInstances.add(generatedComponentInstance);
                    }
                }
            });

        return componentInstances;
    }

    private static ComponentInstance searchForWebAppsInTomcatDirectory(ComponentInstance inputComponentInstance, ComponentInstance tomcatInstance) {
        Session session = buildSessionForPuppetAgent(inputComponentInstance);
        if (session != null) {
            String installedWebApp = executeCommand(session, Commands.SEARCH_FOR_WARS);
            if (installedWebApp != null) {
                return generateComponentInstanceFromWebApp(installedWebApp, tomcatInstance);
            }
        }
        return null;
    }

    private static String retrieveHypervisorName(ComponentInstance inputComponentInstance) {
        Session session = buildSessionForPuppetAgent(inputComponentInstance);
        if (session != null) {
            String hypervisorKeyValuePair = executeCommand(session, Commands.GET_HYPERVISOR);
            if (hypervisorKeyValuePair != null) {
                return generateHypervisorName(hypervisorKeyValuePair);
            }
        }
        return null;
    }

    private static String generateHypervisorName(String hypervisorKeyValuePair) {
        return hypervisorKeyValuePair.substring(hypervisorKeyValuePair.indexOf(":") + 1).trim();
    }

    private static ComponentInstance generateComponentInstanceFromWebApp(String webApp, ComponentInstance tomcatInstance) {
        ComponentInstance componentInstance = new ComponentInstance();
        componentInstance.setId(UUID.randomUUID().toString());
        componentInstance.setName(webApp);
        componentInstance.setState(InstanceState.InstanceStateForComponentInstance.CREATED);
        componentInstance.setType(ComponentType.Web_Application);
        componentInstance.setInstanceProperties(Collections.singletonList(new InstanceProperty(Constants.TYPE, String.class.getSimpleName(), "JavaApp")));
        RelationInstance relationInstance = new RelationInstance();
        relationInstance.setType(RelationType.HostedOn);
        relationInstance.setTargetInstance(tomcatInstance.getId());
        relationInstance.setId(UUID.randomUUID().toString());
        componentInstance.setRelationInstances(Collections.singletonList(relationInstance));

        return componentInstance;
    }

    private static boolean checkIfResourceEventEntryIsSuitable(ResourceEventEntry resourceEventEntry) {
        return isNotNull(resourceEventEntry) && isPackageEntry(resourceEventEntry) && resourceEventEntry.getStatus() == PuppetResourceStatus.success;
    }

    private static boolean isNotNull(ResourceEventEntry resourceEventEntry) {
        return resourceEventEntry.getResource_title() != null && resourceEventEntry.getResource_type() != null && resourceEventEntry.getStatus() != null;
    }

    private static boolean isPackageEntry(ResourceEventEntry resourceEventEntry) {
        return resourceEventEntry.getResource_type().equals(ResourceType.Package);
    }

    private static boolean isSucceeded(String status) {
        return status.equals("success");
    }

    private static Session buildSessionForPuppetAgent(ComponentInstance puppetAgentInstance) {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(null, puppetAgentInstance.getInstanceProperties().stream()
                    .filter(prop -> prop.getKey().equals(String.valueOf(PropertyKey.Compute.private_key)))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("No private key found!"))
                    .getInstanceValue().toString().getBytes(),
                puppetAgentInstance.getInstanceProperties().stream()
                    .filter(prop -> prop.getKey().equals(String.valueOf(PropertyKey.Compute.public_key)))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("No public key found!"))
                    .getInstanceValue().toString().getBytes(),
                null
            );
            Session session = jsch.getSession(
                "ubuntu",
                puppetAgentInstance.getInstanceProperties().stream()
                    .filter(prop -> prop.getKey().equals(String.valueOf(PropertyKey.Compute.public_address)))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("No public address found!"))
                    .getInstanceValue().toString(),
                22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            return session;
        } catch (JSchException | RuntimeException e) {
            logger.error("Failed to connect to Puppet agent. Continue", e);
        }
        return null;
    }

    private static String executeCommand(Session session, String command) {
        try {
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

            channelExec.setCommand(command);
            channelExec.connect();

            return reader.readLine();
        } catch (IOException | JSchException e) {
            logger.error("Failed to retrieve installed WARs on Puppet agent. Continue", e);
        }
        return null;
    }

    private static ComponentInstance generateComponentInstanceFromResourceEventEntry(ResourceEventEntry entry, ComponentInstance componentInstance, String certName) {
        ComponentInstance packageComponent = new ComponentInstance();
        packageComponent.setId(UUID.randomUUID().toString());
        packageComponent.setName(entry.getResource_title());
        packageComponent.setState(InstanceState.InstanceStateForComponentInstance.CREATED);
        InstanceProperty typeProp = new InstanceProperty(Constants.TYPE, String.class.getSimpleName(), entry.getResource_title());
        packageComponent.setInstanceProperties(Collections.singletonList(typeProp));
        packageComponent.setType(getComponentTypeForPuppetPackage(entry.getResource_title()));
        RelationInstance relationInstance = new RelationInstance();
        relationInstance.setType(RelationType.HostedOn);
        relationInstance.setTargetInstance(componentInstance.getId());
        relationInstance.setId(UUID.randomUUID().toString());
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
            case "tomcat":
                return ComponentType.Tomcat;
            case "mongodb":
                return ComponentType.DBMS;
            case "java":
            default:
                return ComponentType.Software_Component;
        }
    }
}

package io.github.edmm.plugins.terraform.resourcehandlers.openstack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.model.DeploymentTechnologyDescriptor;
import io.github.edmm.model.DiscoveryPluginDescriptor;
import io.github.edmm.plugins.terraform.resourcehandlers.ResourceHandler;
import io.github.edmm.plugins.terraform.resourcehandlers.TerraformInstance;
import io.github.edmm.plugins.terraform.resourcehandlers.TerraformInstanceResource;
import io.github.edmm.plugins.terraform.resourcehandlers.ec2.KeyMapper;
import io.github.edmm.util.Constants;
import io.github.edmm.util.Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenStackInstanceHandler implements ResourceHandler {
    private final static Logger logger = LoggerFactory.getLogger(OpenStackInstanceHandler.class);

    private static final String PROPERTY_TYPE = "type";

    private final TOSCATransformer toscaTransformer;
    private final DeploymentTechnologyDescriptor terraformDeploymentTechnology;
    private final DiscoveryPluginDescriptor terraformDiscoveryPlugin;
    private final KeyMapper keyMapper;

    public OpenStackInstanceHandler(
        TOSCATransformer toscaTransformer,
        DeploymentTechnologyDescriptor terraformDeploymentTechnology,
        DiscoveryPluginDescriptor terraformDiscoveryPlugin,
        KeyMapper keyMapper) {
        this.toscaTransformer = Objects.requireNonNull(toscaTransformer);
        this.terraformDeploymentTechnology = Objects.requireNonNull(terraformDeploymentTechnology);
        this.terraformDiscoveryPlugin = Objects.requireNonNull(terraformDiscoveryPlugin);
        this.keyMapper = keyMapper;
    }

    @Override
    public boolean canHandleResource(Map<String, Object> resource) {
        boolean instanceType = Optional.ofNullable(resource.get(PROPERTY_TYPE))
            .map(Object::toString)
            .map(s -> s.contains("openstack_compute_instance"))
            .orElse(false);

        if (!instanceType) {
            return false;
        }

        try {
            new ObjectMapper().convertValue(resource, OpenStackInstanceResource.class);
        } catch (IllegalArgumentException e) {
            // assume we cannot handle the resource, if jackson conversion fails
            return false;
        }

        return true;
    }

    @Override
    public void addResourceToTemplate(TServiceTemplate serviceTemplate, Map<String, Object> resource) {
        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
        if (topologyTemplate == null) {
            throw new IllegalArgumentException("No topology template specified");
        }

        List<String> managedNodeIds = new ArrayList<>();
        List<String> discoveredIds = new ArrayList<>();

        OpenStackInstanceResource ec2InstanceResource = new ObjectMapper().convertValue(resource, OpenStackInstanceResource.class);

        for (TerraformInstance<OpenStackInstanceAttributes> curInstance : ec2InstanceResource.getInstances()) {
            OpenStackInstanceAttributes attributes = curInstance.getAttributes();
            String publicIp = attributes.access_ip_v4;

            List<TNodeTemplate> matchingNodes = topologyTemplate.getNodeTemplates()
                .stream()
                .filter(tNodeTemplate -> Optional.ofNullable(tNodeTemplate.getProperties())
                    .filter(props -> props instanceof TEntityTemplate.WineryKVProperties)
                    .map(kvProperties -> ((TEntityTemplate.WineryKVProperties) kvProperties).getKVProperties().get(Constants.VMIP))
                    .map(nodeIp -> Objects.equals(nodeIp, publicIp))
                    .orElse(false))
                .toList();

            TNodeTemplate instanceNode;
            if (matchingNodes.size() > 1) {
                logger.warn("Found |{}| > 1 suitable node templates in topology template. Defaulting to the first found",
                    matchingNodes.size());
                instanceNode = matchingNodes.get(0);
            } else if (matchingNodes.size() == 1) {
                logger.info("Found a suitable node template in topology template. Using it.");
                instanceNode = matchingNodes.get(0);
            } else {
                logger.info("Could not find a suitable node template in topology template. Creating a new one.");
                String[] imageName = attributes.image_name.split("\\s");
                TNodeType computeNodeType = toscaTransformer.getComputeNodeType(imageName[0], imageName[1]);
                instanceNode = ModelUtilities.instantiateNodeTemplate(computeNodeType);
                instanceNode.setId(imageName[0] + attributes.id);
                instanceNode.setName(attributes.image_name);

                topologyTemplate.addNodeTemplate(instanceNode);
            }

            Map<String, String> propertiesForInstance = new HashMap<>();
            propertiesForInstance.put(Constants.VMTYPE, attributes.flavor_name);
            String keyName = attributes.key_pair;
            propertiesForInstance.put(Constants.VM_KEY_PAIR_NAME, keyName);
            keyMapper.getPrivateKeyByName(keyName)
                .ifPresent(key -> propertiesForInstance.put(Constants.VM_PRIVATE_KEY, key));
            propertiesForInstance.put(Constants.VM_INSTANCE_ID, attributes.id);
            propertiesForInstance.put(Constants.VMIP, attributes.access_ip_v4);

            Util.populateNodeTemplateProperties(instanceNode, propertiesForInstance);

            managedNodeIds.add(instanceNode.getId());

            managedNodeIds.addAll(terraformDeploymentTechnology.getManagedIds());
            terraformDeploymentTechnology.setManagedIds(managedNodeIds);

            discoveredIds.addAll(terraformDiscoveryPlugin.getDiscoveredIds());
            terraformDiscoveryPlugin.setDiscoveredIds(discoveredIds);
        }
    }

    private static class OpenStackInstanceResource extends TerraformInstanceResource<OpenStackInstanceAttributes> {
    }
}

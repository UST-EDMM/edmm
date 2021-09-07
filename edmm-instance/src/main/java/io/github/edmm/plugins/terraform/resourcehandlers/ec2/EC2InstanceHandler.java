package io.github.edmm.plugins.terraform.resourcehandlers.ec2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.model.ToscaDeploymentTechnology;
import io.github.edmm.plugins.terraform.resourcehandlers.ResourceHandler;
import io.github.edmm.util.Constants;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EC2InstanceHandler implements ResourceHandler {
    private final static Logger logger = LoggerFactory.getLogger(EC2InstanceHandler.class);

    private static final String PROPERTY_TYPE = "type";

    private final TOSCATransformer toscaTransformer;
    private final ToscaDeploymentTechnology terraformDeploymentTechnology;

    public EC2InstanceHandler(
        TOSCATransformer toscaTransformer, ToscaDeploymentTechnology terraformDeploymentTechnology) {
        this.toscaTransformer = Objects.requireNonNull(toscaTransformer);
        this.terraformDeploymentTechnology = terraformDeploymentTechnology;
    }

    @Override
    public boolean canHandleResource(Map<String, Object> resource) {
        boolean instanceType = Optional.ofNullable(resource.get(PROPERTY_TYPE))
            .map(Object::toString)
            .map(s -> s.endsWith("instance"))
            .orElse(false);

        if (!instanceType) {
            return false;
        }

        try {
            EC2InstanceResource ec2InstanceResource = new ObjectMapper().convertValue(resource,
                EC2InstanceResource.class);
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

        EC2InstanceResource ec2InstanceResource = new ObjectMapper().convertValue(resource, EC2InstanceResource.class);

        for (EC2Instance curInstance : ec2InstanceResource.getInstances()) {
            EC2InstanceAttributes attributes = curInstance.getAttributes();
            String publicIp = attributes.getPublicIp();
            String privateIp = attributes.getPrivateIp();

            List<TNodeTemplate> matchingNodes = topologyTemplate.getNodeTemplates()
                .stream()
                .filter(tNodeTemplate -> Optional.ofNullable(tNodeTemplate.getProperties())
                    .map(TEntityTemplate.Properties::getKVProperties)
                    .map(kvProperties -> kvProperties.get(Constants.VMIP))
                    .map(nodeIp -> Objects.equals(nodeIp, publicIp) || Objects.equals(nodeIp, privateIp))
                    .orElse(false))
                .collect(Collectors.toList());

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
                TNodeType computeNodeType = toscaTransformer.getComputeNodeType(attributes.getAmi(), "");
                instanceNode = ModelUtilities.instantiateNodeTemplate(computeNodeType);
                instanceNode.setId(attributes.getArn());
                instanceNode.setName(attributes.getId());

                topologyTemplate.addNodeTemplate(instanceNode);
            }

            Map<String, String> propertiesForInstance = new HashMap<>();
            propertiesForInstance.put(Constants.VMTYPE, attributes.getInstanceType());
            propertiesForInstance.put(Constants.VM_KEY_PAIR_NAME, attributes.getKeyName());
            propertiesForInstance.put(Constants.VM_INSTANCE_ID, attributes.getId());
            propertiesForInstance.put(Constants.VMIP, attributes.getPublicIp());
            propertiesForInstance.put(Constants.EC_2_AMI, attributes.getAmi());

            populateNodeTemplateProperties(instanceNode, propertiesForInstance);

            managedNodeIds.add(instanceNode.getId());

            managedNodeIds.addAll(terraformDeploymentTechnology.getManagedIds());
            terraformDeploymentTechnology.setManagedIds(managedNodeIds);
        }
    }
}

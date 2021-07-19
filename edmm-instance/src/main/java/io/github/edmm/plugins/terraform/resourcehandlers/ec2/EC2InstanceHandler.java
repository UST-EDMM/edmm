package io.github.edmm.plugins.terraform.resourcehandlers.ec2;

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
import io.github.edmm.plugins.terraform.resourcehandlers.ResourceHandler;
import io.github.edmm.util.Constants;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EC2InstanceHandler implements ResourceHandler {
    private final static Logger logger = LoggerFactory.getLogger(EC2InstanceHandler.class);

    private static final String PROPERTY_TYPE = "type";

    private final TOSCATransformer toscaTransformer;
    private final String terraformNodeId;

    public EC2InstanceHandler(TOSCATransformer toscaTransformer, String terraformNodeId) {
        this.toscaTransformer = Objects.requireNonNull(toscaTransformer);
        this.terraformNodeId = Objects.requireNonNull(terraformNodeId);
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
        TNodeTemplate terraformNode = topologyTemplate.getNodeTemplate(terraformNodeId);
        if (terraformNode == null) {
            throw new IllegalArgumentException("Could not find terraform node");
        }

        EC2InstanceResource ec2InstanceResource = new ObjectMapper().convertValue(resource, EC2InstanceResource.class);

        for (EC2Instance curInstance : ec2InstanceResource.getInstances()) {
            EC2InstanceAttributes attributes = curInstance.getAttributes();
            String publicIp = attributes.getPublicIp();

            List<TNodeTemplate> matchingNodes = topologyTemplate.getNodeTemplates()
                .stream()
                .filter(tNodeTemplate -> Optional.ofNullable(tNodeTemplate.getProperties())
                    .map(TEntityTemplate.Properties::getKVProperties)
                    .map(kvProperties -> kvProperties.get(Constants.VMIP))
                    .map(nodeIp -> Objects.equals(nodeIp, publicIp))
                    .orElse(false))
                .collect(Collectors.toList());

            TNodeTemplate instanceNode;
            if (matchingNodes.size() > 1) {
                logger.warn("Found |{}| > 1 suitable node templates in topology template. Defaulting to the first found",
                    matchingNodes.size());
                instanceNode = matchingNodes.get(0);
            } else if (matchingNodes.size() == 1) {
                instanceNode = matchingNodes.get(0);
            } else {
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

            populateNodeTemplateProperties(instanceNode, propertiesForInstance);

            ModelUtilities.createRelationshipTemplateAndAddToTopology(instanceNode,
                terraformNode,
                Constants.deployedByRelationshipType,
                topologyTemplate);
        }
    }
}

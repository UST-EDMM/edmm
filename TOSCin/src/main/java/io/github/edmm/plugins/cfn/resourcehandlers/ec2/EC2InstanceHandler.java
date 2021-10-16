package io.github.edmm.plugins.cfn.resourcehandlers.ec2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.model.ToscaDeploymentTechnology;
import io.github.edmm.model.ToscaDiscoveryPlugin;
import io.github.edmm.plugins.cfn.resourcehandlers.ResourceHandler;
import io.github.edmm.plugins.cfn.util.CfnTypeConstants;
import io.github.edmm.util.Constants;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudformation.model.StackResourceDetail;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EC2InstanceHandler implements ResourceHandler {
    private final static Logger logger = LoggerFactory.getLogger(EC2InstanceHandler.class);
    private final TOSCATransformer toscaTransformer;
    private final KeyMapper keyMapper;
    private final ToscaDeploymentTechnology cfnDeploymentTechnology;
    private final ToscaDiscoveryPlugin cfnDiscoveryPlugin;
    private final AmazonEC2 ec2Client;

    public EC2InstanceHandler(TOSCATransformer toscaTransformer,
                              ProfileCredentialsProvider credentialsProvider,
                              String region,
                              KeyMapper keyMapper,
                              ToscaDeploymentTechnology cfnDeploymentTechnology,
                              ToscaDiscoveryPlugin cfnDiscoveryPlugin) {
        this.toscaTransformer = Objects.requireNonNull(toscaTransformer);
        this.keyMapper = Objects.requireNonNull(keyMapper);
        this.cfnDeploymentTechnology = Objects.requireNonNull(cfnDeploymentTechnology);
        this.cfnDiscoveryPlugin = Objects.requireNonNull(cfnDiscoveryPlugin);
        Objects.requireNonNull(credentialsProvider);
        Objects.requireNonNull(region);
        ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region).build();
    }

    @Override
    public boolean canHandleResource(String resourceType) {
        return CfnTypeConstants.EC2_INSTANCE.equals(resourceType);
    }

    @Override
    public void addResourceToTemplate(TServiceTemplate serviceTemplate, StackResourceDetail resource) {
        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
        if (topologyTemplate == null) {
            throw new IllegalArgumentException("No topology template specified");
        }

        List<String> managedNodeIds = new ArrayList<>();
        List<String> discoveredIds = new ArrayList<>();

        List<Reservation> reservations = ec2Client.describeInstances(new DescribeInstancesRequest().withInstanceIds(resource.getPhysicalResourceId()))
            .getReservations();
        if (reservations.size() != 1) {
            throw new IllegalStateException("Expected a single reservation");
        }
        List<Instance> instances = reservations.get(0).getInstances();
        if (instances.size() != 1) {
            throw new IllegalStateException("Expected a single instance");
        }
        Instance instance = instances.get(0);

        String publicIp = instance.getPublicIpAddress();
        String privateIp = instance.getPrivateIpAddress();

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
            TNodeType computeNodeType = toscaTransformer.getComputeNodeType(instance.getImageId(), "");
            instanceNode = ModelUtilities.instantiateNodeTemplate(computeNodeType);
            instanceNode.setId(instance.getInstanceId());
            instanceNode.setName(instance.getInstanceId());

            topologyTemplate.addNodeTemplate(instanceNode);
        }

        Map<String, String> propertiesForInstance = new HashMap<>();
        propertiesForInstance.put(Constants.VMTYPE, instance.getInstanceType());
        String keyName = instance.getKeyName();
        propertiesForInstance.put(Constants.VM_KEY_PAIR_NAME, keyName);
        keyMapper.getPrivateKeyByName(keyName)
            .ifPresent(key -> propertiesForInstance.put(Constants.VM_PRIVATE_KEY, key));
        propertiesForInstance.put(Constants.VM_INSTANCE_ID, instance.getInstanceId());
        propertiesForInstance.put(Constants.VMIP, publicIp);
        propertiesForInstance.put(Constants.EC_2_AMI, instance.getImageId());

        populateNodeTemplateProperties(instanceNode, propertiesForInstance);

        managedNodeIds.add(instanceNode.getId());
        discoveredIds.add(instanceNode.getId());

        managedNodeIds.addAll(cfnDeploymentTechnology.getManagedIds());
        cfnDeploymentTechnology.setManagedIds(managedNodeIds);

        discoveredIds.addAll(cfnDiscoveryPlugin.getDiscoveredIds());
        cfnDiscoveryPlugin.setDiscoveredIds(discoveredIds);
    }
}

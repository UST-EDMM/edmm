package io.github.edmm.plugins.heat.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.ComponentType;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.edimm.PropertyKey;
import io.github.edmm.plugins.heat.model.StackStatus;
import io.github.edmm.util.CastUtil;
import io.github.edmm.util.Constants;

import com.google.common.net.InetAddresses;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.heat.Resource;

public class HeatResourceHandler {

    private static final int firstEntryIndex = 0;

    public static List<ComponentInstance> getComponentInstances(List<? extends Resource> resources, Map<String, Object> template, OSClient.OSClientV3 osClient) {
        List<ComponentInstance> componentInstances = new ArrayList<>();
        resources.forEach(resource -> {
            Map<String, Object> resourceContent = CastUtil.safelyCastToStringObjectMap(template.get(HeatConstants.RESOURCES));
            componentInstances.add(getComponentInstance(resources, resource, resourceContent, osClient));
        });
        return componentInstances;
    }

    private static ComponentInstance getComponentInstance(List<? extends Resource> resources, Resource resource, Map<String, Object> resourceContent, OSClient.OSClientV3 osClient) {
        String originalType = resource.getType();
        ComponentInstance componentInstance = new ComponentInstance();

        componentInstance.setType(new EDMMTypeMapperImplementation().toComponentType(resource.getType()));
        componentInstance.setId(resource.getPhysicalResourceId());
        componentInstance.setCreatedAt(String.valueOf(resource.getTime()));
        componentInstance.setName(resource.getResourceName());
        componentInstance.setState(StackStatus.StackStatusForComponentInstance.valueOf(resource.getResourceStatus()).toEDIMMComponentInstanceState());
        componentInstance.setInstanceProperties(HeatResourceHandler.getResourceInstanceProperties(resource, resourceContent, componentInstance.getType()));
        // set property with original type string in order to avoid losing this info since we map to EDMM types

        if (componentInstance.getType().equals(ComponentType.Compute)) {
            originalType = getOriginalTypeFromImageIdentifier(resource.getResourceName(), resourceContent, osClient);
            componentInstance.getInstanceProperties().add(new InstanceProperty(String.valueOf(PropertyKey.Compute.public_address), String.class.getSimpleName(), getIpAddress(componentInstance.getId(), osClient)));
            componentInstance.getInstanceProperties().add(new InstanceProperty(String.valueOf(PropertyKey.Compute.public_key), String.class.getSimpleName(), getPublicKey(String.valueOf(componentInstance.getInstanceProperties().stream().filter(prop -> prop.getKey().equals("key_name")).findFirst().get().getInstanceValue()), osClient)));
        }
        componentInstance.getInstanceProperties().add(new InstanceProperty(Constants.TYPE, String.class.getSimpleName(), originalType));
        componentInstance.setRelationInstances(HeatRelationHandler.getRelationInstances(resources, resourceContent, resource));
        componentInstance.setMetadata(HeatMetadataHandler.getComponentMetadata(resource, resourceContent));

        return componentInstance;
    }

    private static String getPublicKey(String keyName, OSClient.OSClientV3 osClient) {
        return osClient.compute().keypairs().get(keyName).getPublicKey();
    }

    private static String getIpAddress(String serverId, OSClient.OSClientV3 osClient) {
        Map<String, List<? extends Address>> ipAddress = osClient.compute().servers().get(serverId).getAddresses().getAddresses();
        for (String key : ipAddress.keySet()) {
            List<? extends Address> ipAddressList = ipAddress.get(key);
            Optional<? extends Address> addressOptional = ipAddressList.stream().filter(ip -> InetAddresses.isInetAddress(ip.getAddr())).findFirst();
            Address address = addressOptional.orElse(null);
            return address.getAddr();
        }
        return null;
    }

    private static String getOriginalTypeFromImageIdentifier(String resourceName, Map<String, Object> resourceContent, OSClient.OSClientV3 osClient) {
        Map<String, Object> propertiesMap = HeatMetadataHandler.getPropertiesMap(HeatMetadataHandler.getResourceMap(resourceContent, resourceName));
        String imageProp = String.valueOf(propertiesMap.get("image"));
        return osClient.imagesV2().get(imageProp).getName();
    }

    private static List<InstanceProperty> getResourceInstanceProperties(Resource resource, Map<String, Object> allResourceContent, ComponentType componentType) {
        Map<String, Object> resourceMap = HeatMetadataHandler.getResourceMap(allResourceContent, resource.getResourceName());
        Map<String, Object> propertiesMap = HeatMetadataHandler.getPropertiesMap(resourceMap);

        return handleResourceInstanceProperties(componentType, propertiesMap);
    }

    private static List<InstanceProperty> handleResourceInstanceProperties(ComponentType componentType, Map<String, Object> propertiesMap) {
        List<InstanceProperty> instanceProperties = new ArrayList<>();

        propertiesMap.forEach((key, value) -> {
            if (isNoResourceInstanceProperty(key)) {
                return;
            }
            instanceProperties.addAll(Objects.requireNonNull(handleResourceInstanceProperty(key, value)));
        });
        EDMMPropertyMapperImplementation propMapper = new EDMMPropertyMapperImplementation();
        instanceProperties.forEach(instanceProperty -> instanceProperty.setKey(propMapper.mapToEDMMPropertyKey(componentType, instanceProperty.getKey())));
        List<InstanceProperty> toBeRemoved = new ArrayList<>();
        for (InstanceProperty instanceProperty : instanceProperties) {
            if (instanceProperty.getKey() == null) {
                toBeRemoved.add(instanceProperty);
            }
        }
        instanceProperties.removeAll(toBeRemoved);
        return instanceProperties;
    }

    private static List<InstanceProperty> handleResourceInstanceProperty(String key, Object value) {
        if (value instanceof String) {
            return Collections.singletonList(handleStringProperty(key, String.valueOf(value)));
        } else if (value instanceof List) {
            return handleListProperty(key, (List) value);
        }
        return Collections.emptyList();
    }

    private static InstanceProperty handleStringProperty(String key, String value) {
        return new InstanceProperty(key, value.getClass().getSimpleName(), value);
    }

    private static List<InstanceProperty> handleListProperty(String key, List<?> value) {
        List<InstanceProperty> instanceProperties = new ArrayList<>();

        if (value.get(firstEntryIndex) instanceof String) {
            value.forEach(entry -> instanceProperties.add(handleStringProperty(key, String.valueOf(entry))));
        } else if (value.get(firstEntryIndex) instanceof Map) {
            value.forEach(entry -> CastUtil.safelyCastToStringStringMap(entry).forEach((pKey, pValue) -> instanceProperties.add(handleStringProperty(key + Constants.DELIMITER + pKey, pValue))));
        }
        return instanceProperties;
    }

    private static boolean isNoResourceInstanceProperty(String key) {
        return key.equals(HeatConstants.METADATA) || key.equals(HeatConstants.TAGS);
    }

}

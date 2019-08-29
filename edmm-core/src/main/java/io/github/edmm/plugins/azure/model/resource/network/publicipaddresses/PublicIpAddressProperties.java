
package io.github.edmm.plugins.azure.model.resource.network.publicipaddresses;

import java.util.Map;

import io.github.edmm.plugins.azure.model.resource.Properties;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PublicIpAddressProperties extends Properties {
    private String publicIpAllocationMethod;
    private Map<String, String> dnsSettings;
}


package io.github.edmm.plugins.azure.model.resource.network.virtualnetworks.subnets;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.resource.Properties;
import io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups.NetworkSecurityGroup;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubnetProperties extends Properties {
    private String addressPrefix;
}

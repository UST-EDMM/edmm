
package io.github.edmm.plugins.azure.model.resource.network.virtualnetworks.subnets;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.resource.Properties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubnetProperties extends Properties {
    private String addressPrefix;
}

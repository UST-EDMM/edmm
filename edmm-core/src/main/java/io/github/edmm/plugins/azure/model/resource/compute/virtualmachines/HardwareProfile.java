package io.github.edmm.plugins.azure.model.resource.compute.virtualmachines;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HardwareProfile {
    /**
     * For valid values check: https://docs.microsoft.com/en-us/azure/templates/microsoft.compute/2019-03-01/virtualmachines#HardwareProfile
     */
    private String vmSize;
}

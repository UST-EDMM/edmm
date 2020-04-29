package io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions;

import io.github.edmm.plugins.azure.model.resource.Properties;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualMachineExtensionProperties extends Properties {
    private String publisher;
    private String type;
    private String typeHandlerVersion;
    private boolean autoUpgradeMinorVersion;
    private CustomScriptSettings settings;
}

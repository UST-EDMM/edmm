package io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.resource.Properties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualMachineExtensionProperties extends Properties {
    private String publisher;
    private String type;
    private String typeHandlerVersion;
    private boolean autoUpgradeMinorVersion;
    private CustomScriptSettings settings;
}

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class CustomScriptSettings {
    private List<String> fileUrls;
    private String commandToExecute;
}

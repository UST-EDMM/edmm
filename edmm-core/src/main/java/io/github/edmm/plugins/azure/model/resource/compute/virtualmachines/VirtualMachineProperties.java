package io.github.edmm.plugins.azure.model.resource.compute.virtualmachines;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.resource.Properties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualMachineProperties extends Properties {
    private HardwareProfile hardwareProfile;
    private OsProfile osProfile;
    private StorageProfile storageProfile;
    private NetworkProfile networkProfile;
}

package io.github.edmm.plugins.azure.model.resource.compute.virtualmachines;

import io.github.edmm.plugins.azure.model.resource.Properties;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualMachineProperties extends Properties {
    private HardwareProfile hardwareProfile;
    private OsProfile osProfile;
    private StorageProfile storageProfile;
    private NetworkProfile networkProfile;
}

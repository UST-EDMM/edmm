package io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions;

import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;

public class VirtualMachineExtension extends Resource {
    public VirtualMachineExtension(String name) {
        super(ResourceTypeEnum.VIRTUAL_MACHINE_EXTENSIONS, name);
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        setApiVersion("2019-03-01");
        setProperties(VirtualMachineExtensionProperties
                .builder()
                .autoUpgradeMinorVersion(true)
                .publisher("Microsoft.Azure.Extensions")
                .type("CustomScript")
                .typeHandlerVersion("2.0")
                // todo setting the settings happens when examining the corresponding operation
                //.settings(CustomScriptSettings.builder().build())
                .build());
    }
}

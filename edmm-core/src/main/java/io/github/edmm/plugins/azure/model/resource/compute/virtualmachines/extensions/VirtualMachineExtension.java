package io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;
import io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.VirtualMachine;

public class VirtualMachineExtension extends Resource {
    public VirtualMachineExtension(VirtualMachine vm, String componentName, String artifactName) {
        super(ResourceTypeEnum.VIRTUAL_MACHINE_EXTENSIONS, String.format("%s_extension_%s", componentName, artifactName));
        List<String> dependencies = new ArrayList<>();
        // Set a dependency on the virtual machine
        dependencies.add(vm.getFullName());
        this.setDependsOn(dependencies);
    }

    @JsonIgnore
    public Optional<String> getScriptPath() {
        return ((VirtualMachineExtensionProperties) getProperties()).getSettings().getFileUrls().stream().findFirst();
    }

    public void setScriptPath(String scriptPath) {
        ((VirtualMachineExtensionProperties) getProperties()).setSettings(CustomScriptSettings
            .builder()
            .fileUrls(Collections.singletonList(scriptPath))
            .commandToExecute(String.format("'sh %s'", scriptPath))
            .build());
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
            // setting the settings happens when examining the corresponding operation
            .build());
    }
}

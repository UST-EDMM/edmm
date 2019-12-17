package io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.VirtualMachine;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnvVarVirtualMachineExtension extends VirtualMachineExtension {

    @JsonIgnore
    private String[] blacklist = {"key_name", "public_key"};

    @JsonIgnore
    @Getter
    private Map<String, String> environmentVariables = new HashMap<>();

    public EnvVarVirtualMachineExtension(VirtualMachine vm) {
        super(vm, vm.getName().replaceFirst("vm_", ""), "env");
        setScriptPath(String.format("./%s/env.sh", vm.getName()));
    }

    public void addEnvironmentVariables(RootComponent component) {
        Map<String, Property> propertyMap = component.getProperties();

        propertyMap.values().stream()
                .filter(p -> !Arrays.asList(blacklist).contains(p.getName()))
                .forEach(p -> {
                    String name = (component.getNormalizedName() + "_" + p.getNormalizedName()).toUpperCase();
                    environmentVariables.put(name, p.getValue());
                });
    }
}

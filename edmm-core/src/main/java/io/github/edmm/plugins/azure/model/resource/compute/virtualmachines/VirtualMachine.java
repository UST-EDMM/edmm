package io.github.edmm.plugins.azure.model.resource.compute.virtualmachines;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.Parameter;
import io.github.edmm.plugins.azure.model.ParameterTypeEnum;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;
import io.github.edmm.plugins.azure.model.resource.network.networkinterfaces.NetworkInterface;
import sun.nio.ch.Net;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualMachine extends Resource {
    public VirtualMachine(String name) {
        super(ResourceTypeEnum.VIRTUAL_MACHINES, name);
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        setApiVersion("2019-03-01");
        final HardwareProfile hardwareProfile = HardwareProfile
                .builder()
                .vmSize("Standard_A0")
                .build();

        final LinuxConfiguration linuxConfiguration = LinuxConfiguration
                .builder()
                .disablePasswordAuthentication(String.format("[equals(parameters('%s_authentication'), 'ssh')]", this.getName()))
                .ssh(SshConfiguratoin
                        .builder()
                        .publicKeys(Collections.singletonList(PublicKey
                                .builder()
                                .keyData(String.format("[parameters('%s_adminKeyOrPassword')]", this.getName()))
                                .path(String.format("[concat('/home/', parameters('%s_adminUserName'), '/.ssh/authorized_keys')]", this.getName()))
                                .build()))
                        .build()
                )
                .build();
        final OsProfile osProfile = OsProfile
                .builder()
                .computerName(String.format("[parameters('%s_computerName')]", this.getName()))
                .adminUsername(String.format("[parameters('%s_adminUserName')]", this.getName()))
                .adminPassword(String.format("[parameters('%s_adminKeyOrPassword')]", this.getName()))
                .linuxConfiguration(linuxConfiguration)
                .build();

        final StorageProfile storageProfile = StorageProfile
                .builder()
                .imageReference(ImageReference
                        .builder()
                        .publisher("Canonical")
                        .offer("UbuntuServer")
                        .sku("14.04.5-LTS")
                        .build())
                .osDisk(OsDisk
                        .builder()
                        .caching(CachingEnum.ReadWrite)
                        .createOptions(CreateOptionsEnum.FromImage)
                        .name(String.format("%s_OSDisk", this.getName()))
                        .build())
                .build();
        final NetworkInterface networkInterface = new NetworkInterface(String.format("%s_networkInterface", this.getName()));

        final NetworkProfile networkProfile = NetworkProfile
                .builder()
                .networkInterfaces(Collections.singletonList(networkInterface))
                .build();

        setProperties(VirtualMachineProperties
                .builder()
                .hardwareProfile(hardwareProfile)
                .networkProfile(networkProfile)
                .osProfile(osProfile)
                .storageProfile(storageProfile)
                .build());
    }

    @Override
    protected Map<String, Parameter> getRequiredParameters() {
        Map<String, Parameter> params = super.getRequiredParameters();
        params.put(this.getName() + "_computerName",
                Parameter.builder()
                        .type(ParameterTypeEnum.STRING)
                        .defaultValue(this.getName() + "_Computer")
                        .build());
        params.put(this.getName() + "_adminUserName",
                Parameter.builder()
                        .type(ParameterTypeEnum.STRING)
                        .defaultValue(this.getName() + "_Admin")
                        .build());
        // todo setting the default value for this parameter happens when examining the Compute node
        params.put(this.getName() + "_adminKeyOrPassword",
                Parameter.builder()
                        .type(ParameterTypeEnum.SECURE_STRING)
                        .build());
        // todo setting the default value for this parameter happens when examining the Compute node
        params.put(this.getName() + "_authentication",
                Parameter.builder()
                        .type(ParameterTypeEnum.BOOLEAN)
                        .build());

        return  params;
    }
}

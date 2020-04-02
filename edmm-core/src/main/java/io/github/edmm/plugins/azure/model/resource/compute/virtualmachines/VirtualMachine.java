package io.github.edmm.plugins.azure.model.resource.compute.virtualmachines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.Parameter;
import io.github.edmm.plugins.azure.model.ParameterTypeEnum;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;
import io.github.edmm.plugins.azure.model.resource.network.networkinterfaces.NetworkInterface;
import io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups.NetworkSecurityGroup;
import io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups.securityrules.SecurityRule;
import io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups.securityrules.SecurityRuleProperties;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualMachine extends Resource {
    public VirtualMachine(String name) {
        super(ResourceTypeEnum.VIRTUAL_MACHINES, name);
    }

    // setting a value for the password or the linux configuration happens when examining the compute node
    public void setAuthentication(boolean isPassword, String passwordOrSsh) {
        if (isPassword) {
            ((VirtualMachineProperties) this.getProperties()).getOsProfile().setAdminPassword(passwordOrSsh);
        } else {
            final LinuxConfiguration linuxConfiguration = LinuxConfiguration
                .builder()
                .disablePasswordAuthentication(true)
                .ssh(SshConfiguratoin
                    .builder()
                    .publicKeys(Collections.singletonList(PublicKey
                        .builder()
                        .keyData(passwordOrSsh)
                        .path(String.format("[concat('/home/', parameters('%s_adminUserName'), '/.ssh/authorized_keys')]", this.getName()))
                        .build()))
                    .build()
                )
                .build();

            ((VirtualMachineProperties) this.getProperties()).getOsProfile().setLinuxConfiguration(linuxConfiguration);
        }
    }

    // adding a port to the security group happens when detecting a node depending on this virtual machine that needs this port
    public void addPort(String targetNodeName, String port) {
        final SecurityRule newRule = new SecurityRule(String.format("sr_%s", targetNodeName));
        ((SecurityRuleProperties) newRule.getProperties()).setTargetPortRange(port);
        NetworkSecurityGroup sg = ((VirtualMachineProperties) this.getProperties())
            .getNetworkProfile()
            .getNetworkInterfaces()
            .get(0)
            .getNetworkSecurityGroup();
        sg.getSecurityRules().add(newRule);
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        setApiVersion("2019-03-01");

        List<String> dependencies = new ArrayList<>();
        dependencies.add("[concat('Microsoft.Storage/storageAccounts/', parameters('storageAccountName'))]");
        dependencies.add("[concat('Microsoft.Network/virtualNetworks/', variables('vnet_name'))]");
        setDependsOn(dependencies);

        final HardwareProfile hardwareProfile = HardwareProfile
            .builder()
            .vmSize("Standard_A0")
            .build();

        final OsProfile osProfile = OsProfile
            .builder()
            .computerName(String.format("[parameters('%s_computerName')]", this.getName()))
            .adminUsername(String.format("[parameters('%s_adminUserName')]", this.getName()))
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
        final NetworkInterface networkInterface = new NetworkInterface(String.format("%s_networkInterface", this.getName()), this.getName());

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
    public Map<String, Parameter> getRequiredParameters() {
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

        for (NetworkInterface nic : ((VirtualMachineProperties) this.getProperties()).getNetworkProfile().getNetworkInterfaces()) {
            Map<String, Parameter> networkInterfaceParams = nic.getRequiredParameters();
            params.putAll(networkInterfaceParams);
        }

        return params;
    }

    @Override
    public Map<String, String> getRequiredVariables() {
        Map<String, String> vars = super.getRequiredVariables();
        for (NetworkInterface nic : ((VirtualMachineProperties) this.getProperties()).getNetworkProfile().getNetworkInterfaces()) {
            Map<String, String> networkInterfaceVars = nic.getRequiredVariables();
            vars.putAll(networkInterfaceVars);
        }

        return vars;
    }
}

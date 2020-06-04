package io.github.edmm.plugins.puppet.util;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.plugins.puppet.model.Fact;

public class PuppetPropertiesHandler {
    static List<InstanceProperty> getComponentInstanceProperties(List<Fact> facts) {
        List<InstanceProperty> instanceProperties = new ArrayList<>();
        facts.forEach(fact -> instanceProperties.add(new InstanceProperty(fact.getName(), fact.getValue().getClass().getSimpleName(), fact.getValue())));
        return instanceProperties;
    }

    public static List<InstanceProperty> getComponentInstanceProperties(String hostName, String user, String ip, String privateKeyLocation, Integer sshPort) {
        List<InstanceProperty> instanceProperties = new ArrayList<>();

        instanceProperties.add(handleHostName(hostName));
        instanceProperties.add(handleUser(user));
        instanceProperties.add(handleIp(ip));
        instanceProperties.add(handlePrivateKeyLocation(privateKeyLocation));
        instanceProperties.add(handleSSHPort(sshPort));

        return instanceProperties;
    }

    private static InstanceProperty handleHostName(String hostName) {
        return new InstanceProperty("hostname", hostName.getClass().getSimpleName(), hostName);
    }

    private static InstanceProperty handleUser(String user) {
        return new InstanceProperty("user", user.getClass().getSimpleName(), user);
    }

    private static InstanceProperty handleIp(String ip) {
        return new InstanceProperty("ip", ip.getClass().getSimpleName(), ip);
    }

    // TODO change this and read the actual private key from the location and set it as property
    private static InstanceProperty handlePrivateKeyLocation(String privateKeyLocation) {
        return new InstanceProperty("privateKeyLocation", privateKeyLocation.getClass().getSimpleName(), privateKeyLocation);
    }

    private static InstanceProperty handleSSHPort(Integer sshPort) {
        return new InstanceProperty("SSHPort", sshPort.getClass().getSimpleName(), sshPort);
    }
}

package io.github.edmm.plugins.puppet.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import io.github.edmm.core.transformation.InstanceTransformationException;
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

    private static InstanceProperty handlePrivateKeyLocation(String privateKeyLocation) {
        return new InstanceProperty("privateKey", privateKeyLocation.getClass().getSimpleName(), readPrivateKeyFileIntoString(privateKeyLocation));
    }

    private static InstanceProperty handleSSHPort(Integer sshPort) {
        return new InstanceProperty("SSHPort", sshPort.getClass().getSimpleName(), sshPort);
    }

    private static String readPrivateKeyFileIntoString(String privateKeyLocation) {
        StringBuilder privateKey = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(privateKeyLocation))) {
            stream.forEach(line -> privateKey.append(line).append("\n"));
        } catch (IOException e) {
            throw new InstanceTransformationException("Failed to convert private key!");
        }
        return privateKey.toString();
    }
}

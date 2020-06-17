package io.github.edmm.plugins.puppet.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.edimm.PropertyKey;
import io.github.edmm.plugins.puppet.model.Fact;

import org.apache.commons.codec.binary.Base64;

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
        instanceProperties.add(handlePublicKey(privateKeyLocation));

        return instanceProperties;
    }

    private static InstanceProperty handleHostName(String hostName) {
        return new InstanceProperty("hostname", hostName.getClass().getSimpleName(), hostName);
    }

    private static InstanceProperty handleUser(String user) {
        return new InstanceProperty("user", user.getClass().getSimpleName(), user);
    }

    private static InstanceProperty handleIp(String ip) {
        return new InstanceProperty(String.valueOf(PropertyKey.Compute.public_address), ip.getClass().getSimpleName(), ip);
    }

    private static InstanceProperty handlePrivateKeyLocation(String privateKeyLocation) {
        return new InstanceProperty("privateKey", privateKeyLocation.getClass().getSimpleName(), readPrivateKeyFileIntoString(privateKeyLocation));
    }

    private static InstanceProperty handleSSHPort(Integer sshPort) {
        return new InstanceProperty("SSHPort", sshPort.getClass().getSimpleName(), sshPort);
    }

    private static InstanceProperty handlePublicKey(String privateKeyLocation) {
        return new InstanceProperty(String.valueOf(PropertyKey.Compute.public_key), String.class.getSimpleName(), generatePublicKeyFromPrivateKeyLocation(privateKeyLocation));
    }

    private static String readPrivateKeyFileIntoString(String privateKeyLocation) {
        StringBuilder privateKey = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(privateKeyLocation))) {
            stream.forEach(line -> privateKey.append(line).append("\n"));
        } catch (IOException e) {
            throw new InstanceTransformationException("Failed to convert private key!", e.getCause());
        }
        return privateKey.toString();
    }

    private static String generatePublicKeyFromPrivateKeyLocation(String privateKeyLocation) {
        String privateKeyString = readPrivateKeyFileIntoString(privateKeyLocation);
        privateKeyString = privateKeyString.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
        privateKeyString = privateKeyString.replace("-----END RSA PRIVATE KEY-----", "");
        try {
            byte[] decoded = Base64.decodeBase64(privateKeyString);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
            );
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            RSAPrivateCrtKey privateCrtKey = (RSAPrivateCrtKey) privateKey;
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privateCrtKey.getModulus(), privateCrtKey.getPublicExponent());
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();

            return encoder.encodeToString(publicKey.getEncoded());
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new InstanceTransformationException("Failed to convert public key", e.getCause());
        }
    }
}

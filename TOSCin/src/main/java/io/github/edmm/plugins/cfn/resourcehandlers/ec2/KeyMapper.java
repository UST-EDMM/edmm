package io.github.edmm.plugins.cfn.resourcehandlers.ec2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.github.edmm.util.Util;

import org.apache.commons.lang3.StringUtils;

public class KeyMapper {
    private final Map<String, String> privKeyFilesByName;

    public KeyMapper() {
        privKeyFilesByName = new HashMap<>();
        initPrivKeys();
    }

    private void initPrivKeys() {
        privKeyFilesByName.put("puppet-master-keys", "C:\\Users\\rosso\\.ssh\\puppet-master-keys.pem");
    }

    public Optional<String> getPrivateKeyByName(String keyName) {
        return Optional.ofNullable(keyName)
            .map(privKeyFilesByName::get)
            .map(Util::readFromFile)
            .filter(StringUtils::isNotBlank);
    }
}

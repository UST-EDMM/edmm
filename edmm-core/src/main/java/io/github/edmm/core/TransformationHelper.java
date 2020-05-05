package io.github.edmm.core;

import java.util.Map;

import io.github.edmm.model.Property;

import org.apache.commons.io.FilenameUtils;

public abstract class TransformationHelper {

    public static final String[] PROPERTY_BLACKLIST = {"*key_name*", "*public_key*"};

    static boolean matchesBlacklist(Map.Entry<String, Property> properties) {
        for (String value : PROPERTY_BLACKLIST) {
            if (FilenameUtils.wildcardMatch(properties.getKey(), value)) {
                return true;
            }
        }
        return false;
    }
}

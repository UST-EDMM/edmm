package io.github.edmm.plugins.kubernetes.util;

import io.github.edmm.core.plugin.EDMMPropertyMapper;
import io.github.edmm.model.edimm.PropertyKey;
import io.github.edmm.util.Constants;

public class EDMMPropertyMapperImplementation implements EDMMPropertyMapper {
    @Override
    public String toAuth0PropertyKey(String specificPropertyKey) {
        return null;
    }

    @Override
    public String toComputePropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.Auth0.type);
        } else if (specificPropertyKey.equals(KubernetesConstants.POD_IP)) {
            return String.valueOf(PropertyKey.Compute.public_address);
        } else if (specificPropertyKey.equals(KubernetesConstants.IMAGE)) {
            return String.valueOf(PropertyKey.Compute.os_family);
        }
        return null;
    }

    @Override
    public String toDBMSPropertyKey(String specificPropertyKey) {
        return null;
    }

    @Override
    public String toDBaaSPropertyKey(String specificPropertyKey) {
        return null;
    }

    @Override
    public String toDatabasePropertyKey(String specificPropertyKey) {
        return null;
    }

    @Override
    public String toMySQL_DBMSPropertyKey(String specificPropertyKey) {
        return null;
    }

    @Override
    public String toMySQL_DatabasePropertyKey(String specificPropertyKey) {
        return null;
    }

    @Override
    public String toPaaSPropertyKey(String specificPropertyKey) {
        return null;
    }

    @Override
    public String toPlatformPropertyKey(String specificPropertyKey) {
        return null;
    }

    @Override
    public String toSaaSPropertyKey(String specificPropertyKey) {
        return null;
    }

    @Override
    public String toSoftware_ComponentPropertyKey(String specificPropertyKey) {
        return null;
    }

    @Override
    public String toTomcatPropertyKey(String specificPropertyKey) {
        return null;
    }

    @Override
    public String toWeb_ApplicationPropertyKey(String specificPropertyKey) {
        return null;
    }

    @Override
    public String toWeb_ServerPropertyKey(String specificPropertyKey) {
        return null;
    }
}

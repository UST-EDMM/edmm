package io.github.edmm.plugins.puppet.util;

import io.github.edmm.core.plugin.EDMMPropertyMapper;
import io.github.edmm.model.edimm.PropertyKey;
import io.github.edmm.util.Constants;

public class EDMMPropertyMapperImplementation implements EDMMPropertyMapper {

    @Override
    public String toAuth0PropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.Auth0.type);
        }
        // TODO domain, identifier, scopes, client_id, client_secret, region
        return null;
    }

    @Override
    public String toComputePropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.Compute.type);
        } else if (specificPropertyKey.equals(Constants.VMIP)) {
            return String.valueOf(PropertyKey.Compute.public_address);
        } else if (specificPropertyKey.equals(Constants.VM_PUBLIC_KEY)) {
            return String.valueOf(PropertyKey.Compute.public_key);
        } else if (specificPropertyKey.equals(Constants.VM_PRIVATE_KEY)) {
            return String.valueOf(PropertyKey.Compute.private_key);
        } else if (specificPropertyKey.equals(Constants.KEY_NAME)) {
            return String.valueOf(PropertyKey.Compute.key_name);
        }
        return null;
    }

    @Override
    public String toDatabasePropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.Database.type);
        }
        // TODO schemaname, user, password
        return null;
    }

    @Override
    public String toDBaaSPropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.Dbaas.type);
        }
        // TODO instance_type, region
        return null;
    }

    @Override
    public String toDBMSPropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.Dbms.type);
        }
        // TODO port, rootpassword
        return null;
    }

    @Override
    public String toMySQL_DatabasePropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.MysqlDatabase.type);
        }
        // TODO schemaname, user, password
        return null;
    }

    @Override
    public String toMySQL_DBMSPropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.MysqlDbms.type);
        }
        // TODO port, rootpassword
        return null;
    }

    @Override
    public String toPaaSPropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.Paas.type);
        }
        // TODO region, archetype, min_instances, max_instances
        return null;
    }

    @Override
    public String toPlatformPropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.Platform.type);
        }
        // TODO region
        return null;
    }

    @Override
    public String toSaaSPropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.Saas.type);
        }
        // TODO region, client_id, client_secret
        return null;
    }

    @Override
    public String toSoftware_ComponentPropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.SoftwareComponent.type);
        }
        return null;
    }

    @Override
    public String toTomcatPropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.Tomcat.type);
        }
        // TODO port
        return null;
    }

    @Override
    public String toWeb_ApplicationPropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.WebApplication.type);
        }
        return null;
    }

    @Override
    public String toWeb_ServerPropertyKey(String specificPropertyKey) {
        if (specificPropertyKey.equals(Constants.TYPE)) {
            return String.valueOf(PropertyKey.WebApplication.type);
        }
        // TODO port
        return null;
    }
}

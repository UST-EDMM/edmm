package io.github.edmm.core.plugin;

import io.github.edmm.model.edimm.ComponentType;

public interface EDMMPropertyMapper {

    String toAuth0PropertyKey(String specificPropertyKey);

    String toComputePropertyKey(String specificPropertyKey);

    String toDBMSPropertyKey(String specificPropertyKey);

    String toDBaaSPropertyKey(String specificPropertyKey);

    String toDatabasePropertyKey(String specificPropertyKey);

    String toMySQL_DBMSPropertyKey(String specificPropertyKey);

    String toMySQL_DatabasePropertyKey(String specificPropertyKey);

    String toPaaSPropertyKey(String specificPropertyKey);

    String toPlatformPropertyKey(String specificPropertyKey);

    String toSaaSPropertyKey(String specificPropertyKey);

    String toSoftware_ComponentPropertyKey(String specificPropertyKey);

    String toTomcatPropertyKey(String specificPropertyKey);

    String toWeb_ApplicationPropertyKey(String specificPropertyKey);

    String toWeb_ServerPropertyKey(String specificPropertyKey);

    default String mapToEDMMPropertyKey(ComponentType componentType, String specificPropertyKey) {
        switch (componentType) {
            case Auth0:
                return toAuth0PropertyKey(specificPropertyKey);
            case Compute:
                return toComputePropertyKey(specificPropertyKey);
            case Database:
                return toDatabasePropertyKey(specificPropertyKey);
            case DBaaS:
                return toDBaaSPropertyKey(specificPropertyKey);
            case DBMS:
                return toDBMSPropertyKey(specificPropertyKey);
            case MySQL_Database:
                return toMySQL_DatabasePropertyKey(specificPropertyKey);
            case MySQL_DBMS:
                return toMySQL_DBMSPropertyKey(specificPropertyKey);
            case PaaS:
                return toPaaSPropertyKey(specificPropertyKey);
            case Platform:
                return toPlatformPropertyKey(specificPropertyKey);
            case SaaS:
                return toSaaSPropertyKey(specificPropertyKey);
            case Software_Component:
                toSoftware_ComponentPropertyKey(specificPropertyKey);
            case Tomcat:
                toTomcatPropertyKey(specificPropertyKey);
            case Web_Application:
                toWeb_ApplicationPropertyKey(specificPropertyKey);
            case Web_Server:
                toWeb_ServerPropertyKey(specificPropertyKey);
                // this is not possible :-)
            default:
                return null;
        }
    }
}

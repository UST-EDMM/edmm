package io.github.edmm.core.plugin;

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
}

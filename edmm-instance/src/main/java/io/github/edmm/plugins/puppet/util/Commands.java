package io.github.edmm.plugins.puppet.util;

import io.github.edmm.plugins.puppet.model.FactType;

public class Commands {
    private static final String BASE_COMMAND = "curl http://localhost:8080/pdb/query/v4/";
    public static final String GET_NODES = BASE_COMMAND + "nodes";

    private static String getIPAddressFact(String certName) {
        return GET_NODES + "/" + certName + "/facts/ipaddress";
    }

    private static String getOperatingSystemFact(String certName) {
        return GET_NODES + "/" + certName + "/facts/operatingsystem";
    }

    private static String getOperatingSystemReleaseFact(String certName) {
        return GET_NODES + "/" + certName + "/facts/operatingsystemrelease";
    }

    public static String getFactCommandByFactType(String certName, FactType factType) {
        switch (factType) {
            case IPAddress:
                return getIPAddressFact(certName);
            case OperatingSystem:
                return getOperatingSystemFact(certName);
            case OperatingSystemRelease:
                return getOperatingSystemReleaseFact(certName);
        }
        return null;
    }
}

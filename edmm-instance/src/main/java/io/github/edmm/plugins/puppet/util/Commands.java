package io.github.edmm.plugins.puppet.util;

import io.github.edmm.plugins.puppet.model.FactType;

public class Commands {
    private static final String BASE_COMMAND = "curl http://localhost:8080/pdb/query/v4/";
    public static final String GET_MASTER = BASE_COMMAND + "producers";
    public static final String GET_NODES = BASE_COMMAND + "nodes";

    private static String getIPAddressFact(String certName) {
        return buildNodeFactQuery(certName) + "ipaddress";
    }

    private static String getOperatingSystemFact(String certName) {
        return buildNodeFactQuery(certName) + "operatingsystem";
    }

    private static String getOperatingSystemReleaseFact(String certName) {
        return buildNodeFactQuery(certName) + "operatingsystemrelease";
    }

    private static String getEC2MetadataFact(String certName) {
        return buildNodeFactQuery(certName) + "ec2_metadata";
    }

    private static String getSSHRSAKeyFact(String certName) {
        return buildNodeFactQuery(certName) + "sshrsakey";
    }

    private static String getSSHDSAKeyFact(String certName) {
        return buildNodeFactQuery(certName) + "sshdsakey";
    }

    private static String buildNodeFactQuery(String certName) {
        return GET_NODES + "/" + certName + "/facts/";
    }

    public static String getFactCommandByFactType(String certName, FactType factType) {
        switch (factType) {
            case IPAddress:
                return getIPAddressFact(certName);
            case OperatingSystem:
                return getOperatingSystemFact(certName);
            case OperatingSystemRelease:
                return getOperatingSystemReleaseFact(certName);
            case EC2Metadata:
                return getEC2MetadataFact(certName);
            case SSHDSAKey:
                return getSSHDSAKeyFact(certName);
            case SSHRSAKey:
                return getSSHRSAKeyFact(certName);
        }
        return null;
    }
}

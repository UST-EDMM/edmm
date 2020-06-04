package io.github.edmm.plugins.puppet.util;

import io.github.edmm.plugins.puppet.model.FactType;

public class Commands {
    private static final String CURL_COMMAND = "curl ";
    private static final String API = "http://localhost:8080/pdb/query/v4/";
    private static final String BASE_COMMAND = CURL_COMMAND + API;
    public static final String GET_MASTER = BASE_COMMAND + "producers";
    public static final String GET_NODES = BASE_COMMAND + "nodes";
    public static final String GET_VERSION = "/opt/puppetlabs/bin/puppet --version";
    // only linux is allowed as OS for puppet master, so this is fine
    public static final String GET_CREATED_AT_TIMESTAMP = "stat -c %Z /proc/";

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
            default:
                return null;
        }
    }

    public static String getNodeStateByReportHash(String reportHash) {
        return CURL_COMMAND + "-X GET " + API + "reports -d 'query=[\"extract\", [\"status\"], [\"~\", \"hash\", \"" + reportHash + "\"]]'";
    }
}

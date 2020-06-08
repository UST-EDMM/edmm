package io.github.edmm.plugins.puppet.util;

import io.github.edmm.plugins.puppet.model.FactType;

public class Commands {
    private static final String CURL_COMMAND = "curl ";
    private static final String API = "http://localhost:8080/pdb/query/v4/";
    private static final String BASE_COMMAND = CURL_COMMAND + API;
    // TODO: make this more stable and less brittle e.g. by searching actively for where puppet modules are stored on master, and by checking environment, but for now its ok
    public static final String COPY_PUBLIC_KEY = "sudo cp ~/.ssh/puppet.pub /etc/puppetlabs/code/environments/production/modules/edimm_ssh/files/puppet.pub";
    private static final String SSH_KEY_GENERATION_PREFIX = "ssh-keygen -f ~/.ssh/";
    private static final String SSH_KEY_GENERATION_SUFFIX = " -t rsa -N ''";
    public static final String GET_MASTER = BASE_COMMAND + "producers";
    public static final String GET_NODES = BASE_COMMAND + "nodes";
    public static final String GET_VERSION = "/opt/puppetlabs/bin/puppet --version";
    // only linux is allowed as OS for puppet master, so linux specific command is fine
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
            default:
                return null;
        }
    }

    public static String generateSSHKeyPairWithCertName(String certName) {
        return Commands.SSH_KEY_GENERATION_PREFIX + certName + SSH_KEY_GENERATION_SUFFIX;
    }

    public static String getNodeStateByReportHash(String reportHash) {
        return CURL_COMMAND + "-X GET " + API + "reports -d 'query=[\"extract\", [\"status\"], [\"~\", \"hash\", \"" + reportHash + "\"]]'";
    }
}

package io.github.edmm.plugins.puppet.util;

import io.github.edmm.plugins.puppet.model.FactType;

// TODO make commands less brittle by actively searching for location of things like puppet executable
class Commands {
    private static final String CURL_COMMAND = "curl ";
    private static final String API = "http://localhost:8080/pdb/query/v4/";
    private static final String BASE_COMMAND = CURL_COMMAND + API;
    private static final String EDIMM_ZIP_FILE = "edimm_ssh.zip";

    static final String COPY_PUBLIC_KEY = "sudo cp ~/.ssh/puppet.pub /etc/puppetlabs/code/environments/production/modules/edimm_ssh/files/puppet.pub";
    static final String UNZIP_PUPPET_MODULE = "unzip " + EDIMM_ZIP_FILE;
    static final String DELETE_ZIP = "rm " + EDIMM_ZIP_FILE;
    static final String MOVE_PUPPET_MODULE = "sudo mv edimm_ssh /etc/puppetlabs/code/environments/production/modules/edimm_ssh";
    static final String EXECUTE_HELPER_SCRIPT = "sudo chmod +x edimm_ssh.sh; sudo ./edimm_ssh.sh";

    private static final String SSH_KEY_GENERATION_PREFIX = "ssh-keygen -f ~/.ssh/";
    private static final String SSH_KEY_GENERATION_SUFFIX = " -t rsa -N ''";
    static final String GET_MASTER = BASE_COMMAND + "producers";
    static final String GET_NODES = BASE_COMMAND + "nodes";
    static final String GET_VERSION = "/opt/puppetlabs/bin/puppet --version";
    static final String GET_CREATED_AT_TIMESTAMP = "stat -c %Z /proc/";

    private static String getIPAddressFact(String certName) {
        return buildNodeFactQuery(certName) + String.valueOf(FactType.IPAddress).toLowerCase();
    }

    private static String getOperatingSystemFact(String certName) {
        return buildNodeFactQuery(certName) + String.valueOf(FactType.OperatingSystem).toLowerCase();
    }

    private static String getOperatingSystemReleaseFact(String certName) {
        return buildNodeFactQuery(certName) + String.valueOf(FactType.OperatingSystemRelease).toLowerCase();
    }

    private static String buildNodeFactQuery(String certName) {
        return GET_NODES + "/" + certName + "/facts/";
    }

    static String generateSSHKeyPairWithCertName(String certName) {
        return Commands.SSH_KEY_GENERATION_PREFIX + certName + SSH_KEY_GENERATION_SUFFIX;
    }

    static String getFactCommandByFactType(String certName, FactType factType) {
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
}

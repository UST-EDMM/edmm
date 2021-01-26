package io.github.edmm.plugins.puppet.util;

import io.github.edmm.plugins.puppet.model.FactType;

// TODO make commands less brittle by actively searching for location of things like puppet executable
abstract class Commands {
    static final String SSH_KEY_GENERATION_PREFIX = "ssh-keygen -f ~/.ssh/";
    static final String SSH_KEY_GENERATION_SUFFIX = " -t rsa -N ''";
    static final String CURL_COMMAND = "curl ";
    static final String API = "http://localhost:8080/pdb/query/v4/";
    static final String BASE_COMMAND = CURL_COMMAND + API;
    static final String EDIMM_ZIP_FILE = "edimm_ssh.zip";

    static final String COPY_PUBLIC_KEY = "sudo cp ~/.ssh/puppet.pub /etc/puppetlabs/code/environments/production/modules/edimm_ssh/files/puppet.pub";
    static final String INSTALL_UNZIP = "sudo apt install unzip";
    static final String UNZIP_PUPPET_MODULE = "unzip " + EDIMM_ZIP_FILE;
    static final String DELETE_ZIP = "rm " + EDIMM_ZIP_FILE;
    static final String MOVE_PUPPET_MODULE = "sudo mv edimm_ssh /etc/puppetlabs/code/environments/production/modules/edimm_ssh";
    static final String EXECUTE_HELPER_SCRIPT = "sudo chmod +x edimm_ssh.sh; sudo ./edimm_ssh.sh";
    static final String TRANSFER_KEYS_TO_NODES = "sudo /opt/puppetlabs/bin/puppet job --query 'nodes[certname] {deactivated is null and expired is null}'";

    static final String GET_MASTER = BASE_COMMAND + "producers";
    static final String GET_ALL_REPORTS = BASE_COMMAND + "reports";
    static final String GET_NODES = BASE_COMMAND + "nodes";
    static final String GET_VERSION = "/opt/puppetlabs/bin/puppet --version";
    static final String GET_CREATED_AT_TIMESTAMP = "stat -c %Z /proc/";
    static final String SEARCH_FOR_WARS = "sudo find /var/lib/tomcat8/webapps -type f -name \"*.war\"";
    static final String GET_HYPERVISOR = "sudo dmidecode | grep -i -e product";

    private static String getIPAddressFact(String certName) {
        return factQuery(certName) + "/" + String.valueOf(FactType.IPAddress).toLowerCase();
    }

    private static String getOperatingSystemFact(String certName) {
        return factQuery(certName) + "/" + String.valueOf(FactType.OperatingSystem).toLowerCase();
    }

    private static String getOperatingSystemReleaseFact(String certName) {
        return factQuery(certName) + "/" + String.valueOf(FactType.OperatingSystemRelease).toLowerCase();
    }

    public static String factQuery(String certName) {
        return GET_NODES + "/" + certName + "/facts";
    }

    static String generateSSHKeyPairWithCertName(String certName) {
        return SSH_KEY_GENERATION_PREFIX + certName + SSH_KEY_GENERATION_SUFFIX;
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

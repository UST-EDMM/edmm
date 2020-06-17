package io.github.edmm.plugins.heat.util;

public class HeatConstants {
    // keys to retrieve info from openstack stack result map
    static final String RESOURCES = "resources";
    static final String PROPERTIES = "properties";
    static final String METADATA = "metadata";
    static final String TAGS = "tags";
    static final String DEPENDS_ON = "depends_on";
    static final String TIMEOUT = "timeout_time";
    static final String UPDATED_TIME = "updated_time";
    public static final String VERSION = "heat_template_version";

    static final String DELIMITER = "::";
    private static final String OS = "OS" + DELIMITER;
    static final String Aodh = OS + "Aodh" + DELIMITER;
    static final String Barbican = OS + "Barbican" + DELIMITER;
    static final String Blazar = OS + "Blazar" + DELIMITER;
    static final String Cinder = OS + "Cinder" + DELIMITER;
    static final String Designate = OS + "Designate" + DELIMITER;
    static final String Glance = OS + "Glance" + DELIMITER;
    static final String Heat = OS + "Heat" + DELIMITER;
    static final String Ironic = OS + "Ironic" + DELIMITER;
    static final String Keystone = OS + "Keystone" + DELIMITER;
    static final String Magnum = OS + "Magnum" + DELIMITER;
    static final String Manila = OS + "Manila" + DELIMITER;
    static final String Mistral = OS + "Mistral" + DELIMITER;
    static final String Monasca = OS + "Monasca" + DELIMITER;
    static final String Neutron = OS + "Neutron" + DELIMITER;
    static final String LBaaS = Neutron + "LBaaS" + DELIMITER;
    static final String TaaS = Neutron + "TaaS" + DELIMITER;
    static final String Nova = OS + "Nova" + DELIMITER;
    static final String Octavia = OS + "Octavia" + DELIMITER;
    static final String Sahara = OS + "Sahara" + DELIMITER;
    static final String Senlin = OS + "Senlin" + DELIMITER;
    static final String Swift = OS + "Swift" + DELIMITER;
    static final String Trove = OS + "Trove" + DELIMITER;
    static final String Zaqar = OS + "Zaqar" + DELIMITER;
    static final String Zun = OS + "Zun" + DELIMITER;
}

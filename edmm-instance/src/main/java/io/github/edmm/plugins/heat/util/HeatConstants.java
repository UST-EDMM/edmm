package io.github.edmm.plugins.heat.util;

import io.github.edmm.util.Constants;

public class HeatConstants {
    public static final String VERSION = "heat_template_version";
    // keys to retrieve info from openstack stack result map
    static final String RESOURCES = "resources";
    static final String PROPERTIES = "properties";
    static final String METADATA = "metadata";
    static final String TAGS = "tags";
    static final String FLAVOR = "flavor";
    static final String DEPENDS_ON = "depends_on";
    static final String TIMEOUT = "timeout_time";
    static final String UPDATED_TIME = "updated_time";

    private static final String OS = "OS" + Constants.DELIMITER;
    static final String Aodh = OS + "Aodh" + Constants.DELIMITER;
    static final String Barbican = OS + "Barbican" + Constants.DELIMITER;
    static final String Blazar = OS + "Blazar" + Constants.DELIMITER;
    static final String Cinder = OS + "Cinder" + Constants.DELIMITER;
    static final String Designate = OS + "Designate" + Constants.DELIMITER;
    static final String Glance = OS + "Glance" + Constants.DELIMITER;
    static final String Heat = OS + "Heat" + Constants.DELIMITER;
    static final String Ironic = OS + "Ironic" + Constants.DELIMITER;
    static final String Keystone = OS + "Keystone" + Constants.DELIMITER;
    static final String Magnum = OS + "Magnum" + Constants.DELIMITER;
    static final String Manila = OS + "Manila" + Constants.DELIMITER;
    static final String Mistral = OS + "Mistral" + Constants.DELIMITER;
    static final String Monasca = OS + "Monasca" + Constants.DELIMITER;
    static final String Neutron = OS + "Neutron" + Constants.DELIMITER;
    static final String LBaaS = Neutron + "LBaaS" + Constants.DELIMITER;
    static final String TaaS = Neutron + "TaaS" + Constants.DELIMITER;
    static final String Nova = OS + "Nova" + Constants.DELIMITER;
    static final String Octavia = OS + "Octavia" + Constants.DELIMITER;
    static final String Sahara = OS + "Sahara" + Constants.DELIMITER;
    static final String Senlin = OS + "Senlin" + Constants.DELIMITER;
    static final String Swift = OS + "Swift" + Constants.DELIMITER;
    static final String Trove = OS + "Trove" + Constants.DELIMITER;
    static final String Zaqar = OS + "Zaqar" + Constants.DELIMITER;
    static final String Zun = OS + "Zun" + Constants.DELIMITER;
}

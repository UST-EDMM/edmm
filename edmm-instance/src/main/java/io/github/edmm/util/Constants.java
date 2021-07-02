package io.github.edmm.util;

import javax.xml.namespace.QName;

public abstract class Constants {
    public static final String DELIMITER = "::";
    public static final String TYPE = "type";
    public static final String KEY_NAME = "key_name";

    public static final String VMIP = "VMIP";
    public static final String VM_PRIVATE_KEY = "VMPrivateKey";
    public static final String VM_PUBLIC_KEY = "VMPublicKey";
    public static final String VMTYPE = "VMType";
    public static final String PORT = "port";
    public static final String VM_KEY_PAIR_NAME = "VMKeyPairName";
    public static final String STATE = "State";
    public static final String RUNNING = "Running";
    public static final String VM_INSTANCE_ID = "VMInstanceID";
    public static final String VM_USER_NAME = "VMUserName";
    public static final String PUPPET_ENV = "PuppetEnvironments";
    public static final String PUPPET_MASTER = "MasterIP";
    public static final String PUPPET_MASTER_KEY = "MasterPrivateKey";
    public static final String PUPPET_MASTER_USER = "MasterUserName";
    public static final String PUPPET_MASTER_PORT = "MasterPort";
    public static final String KUBERNETES_CLUSTER_IP = "cluster_ip";

    public static final QName deployedByRelationshipType = QName.valueOf(
        "{https://examples.opentosca.org/edmm/relationshiptypes}deployedBy");
}

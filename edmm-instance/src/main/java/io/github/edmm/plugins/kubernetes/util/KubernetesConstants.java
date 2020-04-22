package io.github.edmm.plugins.kubernetes.util;

public class KubernetesConstants {

    public static final String KEY_DELIMITER = "::";

    // metadata key constants
    public static final String VERSION = "deployment.kubernetes.io/revision";
    public static final String CLUSTER_NAME = "clusterName";
    public static final String DELETION_GRACE_PERIOD_SECONDS = "deletionGracePeriodSeconds";
    public static final String DELETION_TIMESTAMP = "deletionTimestamp";
    public static final String FINALIZERS = "finalizers";
    public static final String GENERATE_NAME = "generateName";
    public static final String GENERATION = "generation";
    public static final String INITIALIZERS = "initializers";
    public static final String NAMESPACE = "namespace";
    public static final String APP = "app";
    public static final String RESOURCE_VERSION = "resourceVersion";
    public static final String LAST_APPLIED_CONFIG = "kubectl.kubernetes.io/last-applied-configuration";

    // deployment status constants
    public static final String REPLICAS = "replicas";
    public static final String AVAILABLE_REPLICAS = "availableReplicas";
    public static final String READY_REPLICAS = "readyReplicas";
    public static final String UPDATED_REPLICAS = "updatedReplicas";
    public static final String UNAVAILABLE_REPLICAS = "unavailableReplicas";
    public static final String COLLISION_COUNT = "collisionCount";
    public static final String OBSERVED_GENERATION = "observedGeneration";

    // container status constants
    public static final String RUNNING = "running";
    public static final String WAITING = "waiting";
    public static final String TERMINATED = "terminated";

    // pod status constants
    public static final String CONTAINER_STATUS = "containerStatus";
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String CONTAINER_ID = "containerId";
    public static final String IMAGE_ID = "imageId";
    public static final String RESTART_COUNT = "restartCount";
    public static final String STATE = "state";
    public static final String HOST_IP  = "hostIP";
    public static final String MESSAGE  = "message";
    public static final String REASON  = "reason";
    public static final String POD_IP  = "podIP";
    public static final String NOMINATED_NODE_NAME  = "nominatedNodeName";
    public static final String PHASE  = "phase";
    public static final String QOS_CLASS  = "qosClass";

    public static final Integer LATEST_STATUS = 0;


}

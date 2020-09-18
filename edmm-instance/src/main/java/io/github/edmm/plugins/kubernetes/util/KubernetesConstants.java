package io.github.edmm.plugins.kubernetes.util;

public class KubernetesConstants {

    // metadata key constants
    public static final String VERSION = "deployment.kubernetes.io/revision";
    public static final String APP = "app";
    static final String KEY_DELIMITER = "::";
    static final String CLUSTER_NAME = "clusterName";
    static final String DELETION_GRACE_PERIOD_SECONDS = "deletionGracePeriodSeconds";
    static final String DELETION_TIMESTAMP = "deletionTimestamp";
    static final String FINALIZERS = "finalizers";
    static final String GENERATE_NAME = "generateName";
    static final String GENERATION = "generation";
    static final String INITIALIZERS = "initializers";
    static final String NAMESPACE = "namespace";
    static final String RESOURCE_VERSION = "resourceVersion";
    static final String LAST_APPLIED_CONFIG = "kubectl.kubernetes.io/last-applied-configuration";
    static final String API_VERSION = "apiVersion";
    static final String KIND = "kind";

    // deployment status constants
    static final String REPLICAS = "replicas";
    static final String AVAILABLE_REPLICAS = "availableReplicas";
    static final String READY_REPLICAS = "readyReplicas";
    static final String UPDATED_REPLICAS = "updatedReplicas";
    static final String UNAVAILABLE_REPLICAS = "unavailableReplicas";
    static final String COLLISION_COUNT = "collisionCount";
    static final String OBSERVED_GENERATION = "observedGeneration";

    // container status constants
    static final String RUNNING = "running";
    static final String WAITING = "waiting";
    static final String TERMINATED = "terminated";

    // pod status constants
    static final String CONTAINER_STATUS = "containerStatus";
    static final String NAME = "name";
    static final String IMAGE = "image";
    static final String CONTAINER_ID = "containerId";
    static final String IMAGE_ID = "imageId";
    static final String RESTART_COUNT = "restartCount";
    static final String STATE = "state";
    static final String HOST_IP = "hostIP";
    static final String MESSAGE = "message";
    static final String REASON = "reason";
    static final String POD_IP = "podIP";
    static final String PORTS = "ports";
    static final String NOMINATED_NODE_NAME = "nominatedNodeName";
    static final String PHASE = "phase";
    static final String QOS_CLASS = "qosClass";

    static final Integer LATEST_STATUS = 0;
}

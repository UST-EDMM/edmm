package io.github.edmm.plugins.cfn.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.edmm.model.Metadata;

import com.amazonaws.services.cloudformation.model.RollbackTrigger;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.StackResourceDetail;

public class CfnMetadataHandler {
    private Stack stack;
    private StackResourceDetail stackResource;
    private Map<String, Object> metadataMap = new LinkedHashMap<>();

    public CfnMetadataHandler(Stack stack) {
        this.stack = stack;
    }

    CfnMetadataHandler(StackResourceDetail stackResource) {
        this.stackResource = stackResource;
    }

    private static String generateMetadataKey(String firstKey, String secondKey, int count, String thirdKey) {
        return firstKey + CfnConstants.DELIMITER + secondKey + String.valueOf(count) + CfnConstants.DELIMITER + CfnConstants.DELIMITER + thirdKey;
    }

    public Metadata getMetadataForDeploymentInstance() {
        this.handleChangeSetId();
        this.handleDeletionTime();
        this.handleLastUpdatedTime();
        this.handleRollbackConfiguration();
        this.handleDisableRollback();
        this.handleNotificationARNs();
        this.handleTimeoutInMinutes();
        this.handleRoleARN();
        this.handleTags();
        this.handleTerminationProtection();
        this.handleParentId();
        this.handleRootId();
        this.handleCapabilities();

        return Metadata.of(this.metadataMap);
    }

    private void handleChangeSetId() {
        if (this.stack.getChangeSetId() != null) {
            this.metadataMap.put(CfnConstants.CHANGE_SET_ID, this.stack.getChangeSetId());
        }
    }

    private void handleDeletionTime() {
        if (this.stack.getDeletionTime() != null) {
            this.metadataMap.put(CfnConstants.DELETION_TIME, this.stack.getDeletionTime());
        }
    }

    private void handleLastUpdatedTime() {
        if (this.stack.getLastUpdatedTime() != null) {
            this.metadataMap.put(CfnConstants.LAST_UPDATED_TIME, this.stack.getLastUpdatedTime());
        }
    }

    private void handleRollbackConfiguration() {
        if (this.stack.getRollbackConfiguration() != null) {
            this.handleMonitoringTimeInMinutes();
            this.handleRollbackTriggers();
        }
    }

    private void handleRollbackTriggers() {
        if (this.stack.getRollbackConfiguration().getRollbackTriggers() != null) {
            AtomicInteger rollbackTriggerCount = new AtomicInteger();
            this.stack.getRollbackConfiguration().getRollbackTriggers().forEach(rollbackTrigger -> {
                this.handleRollbackTriggerARN(rollbackTrigger, rollbackTriggerCount.get());
                this.handleRollbackTriggerType(rollbackTrigger, rollbackTriggerCount.get());
                rollbackTriggerCount.getAndIncrement();
            });
        }
    }

    private void handleRollbackTriggerARN(RollbackTrigger rollbackTrigger, int rollbackTriggerCount) {
        if (rollbackTrigger.getArn() != null) {
            this.metadataMap.put(generateMetadataKey(
                CfnConstants.ROLLBACK_CONFIGURATION, CfnConstants.ROLLBACK_TRIGGER, rollbackTriggerCount, CfnConstants.ARN),
                rollbackTrigger.getArn());
        }
    }

    private void handleRollbackTriggerType(RollbackTrigger rollbackTrigger, int rollbackTriggerCount) {
        if (rollbackTrigger.getType() != null) {
            this.metadataMap.put(generateMetadataKey(
                CfnConstants.ROLLBACK_CONFIGURATION, CfnConstants.ROLLBACK_TRIGGER, rollbackTriggerCount, CfnConstants.TYPE),
                rollbackTrigger.getType());
        }
    }

    private void handleMonitoringTimeInMinutes() {
        if (this.stack.getRollbackConfiguration().getMonitoringTimeInMinutes() != null) {
            this.metadataMap.put(CfnConstants.MONITORING_TIMES_IN_MINUTES,
                this.stack.getRollbackConfiguration().getMonitoringTimeInMinutes());
        }
    }

    private void handleDisableRollback() {
        if (this.stack.getDisableRollback() != null) {
            this.metadataMap.put(CfnConstants.DISABLE_ROLLBACK, this.stack.getDisableRollback());
        }
    }

    private void handleNotificationARNs() {
        if (this.stack.getNotificationARNs() != null) {
            AtomicInteger notificationARNCount = new AtomicInteger();
            this.stack.getNotificationARNs().forEach(notificationARN -> this.metadataMap.put(CfnConstants.NOTIFICATION_ARNS + String.valueOf(notificationARNCount.getAndIncrement()), notificationARN));
        }
    }

    private void handleTimeoutInMinutes() {
        if (this.stack.getTimeoutInMinutes() != null) {
            this.metadataMap.put(CfnConstants.TIMEOUT_IN_MINUTES, this.stack.getTimeoutInMinutes());
        }
    }

    private void handleRoleARN() {
        if (this.stack.getRoleARN() != null) {
            this.metadataMap.put(CfnConstants.ROLE_ARN, this.stack.getRoleARN());
        }
    }

    private void handleTags() {
        if (this.stack.getTags() != null) {
            this.stack.getTags().forEach(tag -> this.metadataMap.put(tag.getKey(), tag.getValue()));
        }
    }

    private void handleTerminationProtection() {
        if (this.stack.getEnableTerminationProtection() != null) {
            this.metadataMap.put(CfnConstants.ENABLE_TERMINATION_PROTECTION, this.stack.getEnableTerminationProtection());
        }
    }

    private void handleParentId() {
        if (this.stack.getParentId() != null) {
            this.metadataMap.put(CfnConstants.PARENT_ID, this.stack.getParentId());
        }
    }

    private void handleRootId() {
        if (this.stack.getRootId() != null) {
            this.metadataMap.put(CfnConstants.ROOT_ID, this.stack.getRootId());
        }
    }

    private void handleCapabilities() {
        if (this.stack.getCapabilities() != null) {
            AtomicInteger capabilityCount = new AtomicInteger();
            this.stack.getCapabilities().forEach(capability -> this.metadataMap.put(CfnConstants.CAPABILITIES + String.valueOf(capabilityCount.getAndIncrement()), capability));
        }
    }

    Metadata getMetadataOfComponentInstance() {
        this.metadataMap.put(CfnConstants.CONFIG, this.stackResource.getMetadata());
        return Metadata.of(this.metadataMap);
    }
}

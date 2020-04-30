package io.github.edmm.plugins.cfn.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.amazonaws.services.cloudformation.model.Stack;
import io.github.edmm.model.Metadata;

public class CfnMetadataHandler {
    Stack stack;
    Map<String, Object> metadataMap = new LinkedHashMap<>();

    public CfnMetadataHandler(Stack stack) {
        this.stack = stack;
    }

    public Metadata getMetadataForDeploymentInstance() {
        if (this.stack.getChangeSetId() != null) {
            this.metadataMap.put(CfnConstants.CHANGE_SET_ID, this.stack.getChangeSetId());
        }
        if (this.stack.getDeletionTime() != null) {
            this.metadataMap.put(CfnConstants.DELETION_TIME, this.stack.getDeletionTime());
        }
        if (this.stack.getLastUpdatedTime() != null) {
            this.metadataMap.put(CfnConstants.LAST_UPDATED_TIME, this.stack.getLastUpdatedTime());
        }
        if (this.stack.getRollbackConfiguration() != null) {
        }
        if (this.stack.getDisableRollback() != null) {
            this.metadataMap.put(CfnConstants.DISABLE_ROLLBACK, this.stack.getDisableRollback());
        }
        if (this.stack.getNotificationARNs() != null) {
        }
        if (this.stack.getTimeoutInMinutes() != null) {
            this.metadataMap.put(CfnConstants.TIMEOUT_IN_MINUTES, this.stack.getTimeoutInMinutes());
        }
        if (this.stack.getRoleARN() != null) {
            this.metadataMap.put(CfnConstants.ROLE_ARN, this.stack.getRoleARN());
        }
        if (this.stack.getTags() != null) {
            this.stack.getTags().forEach(tag -> this.metadataMap.put(tag.getKey(), tag.getValue()));
        }
        if (this.stack.getEnableTerminationProtection() != null) {
            this.metadataMap.put(CfnConstants.ENABLE_TERMINATION_PROTECTION, this.stack.getEnableTerminationProtection());
        }
        if (this.stack.getParentId() != null) {
            this.metadataMap.put(CfnConstants.PARENT_ID, this.stack.getParentId());
        }
        if (this.stack.getRootId() != null) {
            this.metadataMap.put(CfnConstants.ROOT_ID, this.stack.getRootId());
        }
        return Metadata.of(this.metadataMap);
    }
}

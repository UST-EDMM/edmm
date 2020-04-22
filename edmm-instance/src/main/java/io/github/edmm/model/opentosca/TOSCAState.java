package io.github.edmm.model.opentosca;

public class TOSCAState {
    public enum ServiceTemplateInstanceState {
        INITIAL, CREATING, CREATED, DELETING, DELETED, ERROR, MIGRATING, MIGRATED
    }

    public enum NodeTemplateInstanceState {
        INITIAL(RelationshipTemplateInstanceState.INITIAL),
        CREATING(RelationshipTemplateInstanceState.CREATING),
        CREATED(RelationshipTemplateInstanceState.CREATED),
        CONFIGURING(RelationshipTemplateInstanceState.CREATING),
        CONFIGURED(RelationshipTemplateInstanceState.CREATING),
        STARTING(RelationshipTemplateInstanceState.CREATING),
        STARTED(RelationshipTemplateInstanceState.CREATED),
        STOPPING(RelationshipTemplateInstanceState.DELETING),
        STOPPED(RelationshipTemplateInstanceState.DELETED),
        DELETING(RelationshipTemplateInstanceState.DELETING),
        DELETED(RelationshipTemplateInstanceState.DELETED),
        ERROR(RelationshipTemplateInstanceState.ERROR),
        MIGRATED(RelationshipTemplateInstanceState.MIGRATED);

        public RelationshipTemplateInstanceState relationshipTemplateInstanceState;

        NodeTemplateInstanceState(RelationshipTemplateInstanceState relationshipTemplateInstanceState) {
            this.relationshipTemplateInstanceState = relationshipTemplateInstanceState;
        }

        public RelationshipTemplateInstanceState convertToRelationshipTemplateInstanceState() {
            return relationshipTemplateInstanceState;
        }
    }

    public enum RelationshipTemplateInstanceState {
        INITIAL, CREATING, CREATED, DELETING, DELETED, ERROR, MIGRATED
    }
}

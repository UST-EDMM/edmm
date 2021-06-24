package io.github.edmm.plugins.cfn.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.model.edimm.RelationInstance;
import io.github.edmm.model.edimm.RelationType;
import io.github.edmm.plugins.cfn.model.Resource;
import io.github.edmm.plugins.cfn.model.Template;
import io.github.edmm.util.CastUtil;
import io.github.edmm.util.Constants;

import com.amazonaws.services.cloudformation.model.StackResourceDetail;

class CfnStackRelationHandler {

    private final StackResourceDetail stackResource;
    private final List<StackResourceDetail> stackResources;
    private final Template template;
    private final List<RelationInstance> relationInstances = new ArrayList<>();
    private Resource currentResource;

    CfnStackRelationHandler(
        StackResourceDetail stackResource,
        List<StackResourceDetail> stackResources,
        Template template) {
        this.stackResource = stackResource;
        this.stackResources = stackResources;
        this.template = template;
    }

    List<RelationInstance> getRelationInstances() {
        this.extractCurrentResource();
        this.checkAndHandleRelations();

        return this.relationInstances;
    }

    private void extractCurrentResource() {
        this.currentResource = this.template.getResources().get(this.stackResource.getLogicalResourceId());
    }

    private boolean isAtLeastOneRelationExisting() {
        return this.currentResource.getDependsOn() != null;
    }

    private void checkAndHandleRelations() {
        if (this.isAtLeastOneRelationExisting()) {
            this.iterateOverAllRelations();
        }
    }

    private void iterateOverAllRelations() {
        AtomicInteger relationCount = new AtomicInteger();
        this.currentResource.getDependsOn().forEach((dependsOnKey, dependsOnValueObject) ->
            this.handleRelation(dependsOnValueObject, relationCount));
    }

    private void handleRelation(Object dependsOnValueObject, AtomicInteger relationCount) {
        if (dependsOnValueObject instanceof String) {
            this.createRelationInstance(String.valueOf(dependsOnValueObject), relationCount);
        } else if (dependsOnValueObject instanceof List) {
            CastUtil.safelyCastToStringList(dependsOnValueObject).forEach(dependsOnValue
                -> this.createRelationInstance(String.valueOf(dependsOnValue), relationCount));
        }
    }

    private void createRelationInstance(String dependsOnValue, AtomicInteger relationCount) {
        RelationInstance relationInstance = new RelationInstance();
        relationInstance.setId(this.generateIdOfRelation(relationCount));
        relationInstance.setType(RelationType.DependsOn);
        relationInstance.setTargetInstance(this.getTargetInstanceIdOfRelation(dependsOnValue));
        this.relationInstances.add(relationInstance);
    }

    private String generateIdOfRelation(AtomicInteger relationCount) {
        return RelationType.DependsOn + Constants.DELIMITER + relationCount.getAndIncrement();
    }

    private String getTargetInstanceIdOfRelation(String dependsOnValue) {
        return this.stackResources.stream()
            .filter(res -> res.getLogicalResourceId().equals(String.valueOf(dependsOnValue)))
            .findFirst()
            .orElseThrow(InstanceTransformationException::new)
            .getLogicalResourceId();
    }
}

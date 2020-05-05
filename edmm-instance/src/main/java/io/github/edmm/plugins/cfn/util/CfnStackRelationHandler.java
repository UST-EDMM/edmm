package io.github.edmm.plugins.cfn.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.model.edimm.RelationInstance;
import io.github.edmm.plugins.cfn.model.Resource;
import io.github.edmm.plugins.cfn.model.Template;
import io.github.edmm.util.CastUtil;

import com.amazonaws.services.cloudformation.model.StackResourceDetail;
import com.google.gson.internal.LinkedTreeMap;

class CfnStackRelationHandler {

    private StackResourceDetail stackResource;
    private List<StackResourceDetail> stackResources;
    private Resource currentResource;
    private Template template;
    private List<RelationInstance> relationInstances = new ArrayList<>();

    CfnStackRelationHandler(StackResourceDetail stackResource, List<StackResourceDetail> stackResources, Template template) {
        this.stackResource = stackResource;
        this.stackResources = stackResources;
        this.template = template;
    }

    List<RelationInstance> getRelationInstances() {
        this.currentResource = this.template.getResources().get(this.stackResource.getLogicalResourceId());
        if (this.stackResource.getLogicalResourceId().equals("WebServerInstance")) {
            LinkedTreeMap ok = new LinkedTreeMap();
            ok.put("DependsOn", new ArrayList<>(Arrays.asList("WebServerSecurityGroup")));
            this.currentResource.setDependsOn(ok);
        }
        return this.isRelationExisting() ? handleRelations() : Collections.emptyList();
    }

    private List<RelationInstance> handleRelations() {
        AtomicInteger relationCount = new AtomicInteger();
        this.currentResource.getDependsOn().forEach((dependsOnKey, dependsOnValueObject) -> {
            handleRelationValueObject(dependsOnValueObject, relationCount);
        });
        return this.relationInstances;
    }

    private void handleRelationValueObject(Object dependsOnValueObject, AtomicInteger relationCount) {
        if (dependsOnValueObject instanceof String) {
            this.createRelation(String.valueOf(dependsOnValueObject), relationCount);
        } else if (dependsOnValueObject instanceof List) {
            CastUtil.safelyCastToStringList(dependsOnValueObject).forEach(dependsOnValue -> this.createRelation(String.valueOf(dependsOnValue), relationCount));
        }
    }

    private void createRelation(String dependsOnValue, AtomicInteger relationCount) {
        RelationInstance relationInstance = new RelationInstance();
        relationInstance.setId(CfnConstants.DEPENDS_ON + relationCount.getAndIncrement());
        relationInstance.setType(CfnConstants.DEPENDS_ON);
        relationInstance.setTargetInstanceId(this.stackResources.stream()
            .filter(res -> res.getLogicalResourceId().equals(String.valueOf(dependsOnValue)))
            .findFirst()
            .orElseThrow(InstanceTransformationException::new)
            .getPhysicalResourceId());
        this.relationInstances.add(relationInstance);
    }

    private boolean isRelationExisting() {
        return this.currentResource.getDependsOn() != null;
    }
}

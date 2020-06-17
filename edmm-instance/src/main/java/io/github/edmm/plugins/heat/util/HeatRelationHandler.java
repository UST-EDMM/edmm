package io.github.edmm.plugins.heat.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.model.edimm.RelationInstance;
import io.github.edmm.model.edimm.RelationType;
import io.github.edmm.util.CastUtil;
import io.github.edmm.util.Constants;

import org.openstack4j.model.heat.Resource;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

class HeatRelationHandler {

    static List<RelationInstance> getRelationInstances(List<? extends Resource> resources, Map<String, Object> resourceContent, Resource resourceInput) {
        List<String> dependsOnList = CastUtil.safelyCastToStringList(CastUtil.safelyCastToStringObjectMap(resourceContent.get(resourceInput.getResourceName())).get(HeatConstants.DEPENDS_ON));
        List<RelationInstance> relationInstances = new ArrayList<>();
        AtomicInteger relationCount = new AtomicInteger();
        emptyIfNull(dependsOnList).forEach(dependsOnResource -> {
            // TODO: metadata, instance properties, description
            RelationInstance relationInstance = new RelationInstance();
            relationInstance.setType(RelationType.DependsOn);
            relationInstance.setTargetInstanceId(resources.stream()
                .filter(res -> res.getResourceName().equals(dependsOnResource))
                .findFirst()
                .orElseThrow(InstanceTransformationException::new)
                .getPhysicalResourceId());
            relationInstance.setId(RelationType.DependsOn + Constants.DELIMITER + String.valueOf(relationCount.getAndIncrement()));
            relationInstances.add(relationInstance);
        });
        return relationInstances;
    }
}

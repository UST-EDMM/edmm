package io.github.edmm.plugins.heat.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.Metadata;
import io.github.edmm.model.edimm.RelationInstance;
import io.github.edmm.util.CastUtil;
import org.openstack4j.model.heat.Resource;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

class HeatRelationHandler {

    static List<RelationInstance> getRelationInstances(List<? extends Resource> resources, Map<String, Object> resourceContent, Resource resourceInput) {
        List<String> dependsOnList = CastUtil.safelyCastToStringList(CastUtil.safelyCastToStringObjectMap(resourceContent.get(resourceInput.getResourceName())).get(HeatConstants.DEPENDS_ON));
        List<RelationInstance> relationInstances = new ArrayList<>();

        emptyIfNull(dependsOnList).forEach(dependsOnResource -> {
            Integer relationCount = 0;
            // TODO: metadata, instance properties, description, operation
            RelationInstance relationInstance = new RelationInstance();
            relationInstance.setType(HeatConstants.DEPENDS_ON);
            relationInstance.setTargetInstanceId(resources.stream().filter(res -> res.getResourceName().equals(dependsOnResource)).findFirst().get().getPhysicalResourceId());
            relationInstance.setId(HeatConstants.DEPENDS_ON + String.valueOf(relationCount));
            relationInstances.add(relationInstance);
            relationCount++;
        });
        return (relationInstances.size() > 0) ? relationInstances : Collections.emptyList();
    }
}

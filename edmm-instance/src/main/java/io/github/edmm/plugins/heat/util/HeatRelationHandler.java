package io.github.edmm.plugins.heat.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.edimm.RelationInstance;
import org.openstack4j.model.heat.Resource;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class HeatRelationHandler {

    protected static List<RelationInstance> getRelationInstances(List<? extends Resource> resources, Map<String, Map<String, Object>> resourceContent, Resource resourceInput) {
        List<String> dependsOnList = (List<String>) resourceContent.get(resourceInput.getResourceName()).get(HeatConstants.DEPENDS_ON);
        List<RelationInstance> relationInstances = new ArrayList<>();

        emptyIfNull(dependsOnList).forEach(dependsOnResource -> {
            Integer relationCount = 0;
            RelationInstance relationInstance = new RelationInstance();
            relationInstance.setType(HeatConstants.DEPENDS_ON);
            relationInstance.setTargetInstanceId(resources.stream().filter(res -> res.getResourceName().equals(dependsOnResource)).findFirst().get().getPhysicalResourceId());
            relationInstance.setId(HeatConstants.DEPENDS_ON + String.valueOf(relationCount));
            relationInstances.add(relationInstance);
            relationCount++;
        });
        // only return list if not empty, else return null
        return (relationInstances.size() > 0) ? relationInstances : null;
    }

}

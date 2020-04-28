package io.github.edmm.plugins.heat.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.Metadata;
import io.github.edmm.model.edimm.RelationInstance;
import io.github.edmm.util.Util;
import org.openstack4j.model.heat.Resource;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

class HeatRelationHandler {

    static List<RelationInstance> getRelationInstances(List<? extends Resource> resources, Map<String, Map<String, Object>> resourceContent, Resource resourceInput) {
        List<String> dependsOnList = Util.safelyCastToStringList(resourceContent.get(resourceInput.getResourceName()).get(HeatConstants.DEPENDS_ON));
        List<RelationInstance> relationInstances = new ArrayList<>();

        emptyIfNull(dependsOnList).forEach(dependsOnResource -> {
            Integer relationCount = 0;
            RelationInstance relationInstance = new RelationInstance();
            relationInstance.setType(HeatConstants.DEPENDS_ON);
            relationInstance.setTargetInstanceId(resources.stream().filter(res -> res.getResourceName().equals(dependsOnResource)).findFirst().get().getPhysicalResourceId());
            relationInstance.setId(HeatConstants.DEPENDS_ON + String.valueOf(relationCount));
            relationInstance.setMetadata(Metadata.of(Collections.emptyMap()));
            relationInstance.setInstanceProperties(Collections.emptyList());
            relationInstance.setDescription(null);
            relationInstance.setOperations(Collections.emptyList());
            relationInstances.add(relationInstance);
            relationCount++;
        });
        return (relationInstances.size() > 0) ? relationInstances : Collections.emptyList();
    }
}

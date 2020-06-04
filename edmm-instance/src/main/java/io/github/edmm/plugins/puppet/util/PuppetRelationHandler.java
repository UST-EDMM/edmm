package io.github.edmm.plugins.puppet.util;

import java.util.Collections;
import java.util.List;

import io.github.edmm.model.edimm.RelationInstance;
import io.github.edmm.model.edimm.RelationTypes;
import io.github.edmm.plugins.puppet.model.Master;
import io.github.edmm.plugins.puppet.model.Node;

public class PuppetRelationHandler {
    public static List<RelationInstance> getRelationInstances(Master master, Node node) {
        RelationInstance relationInstance = new RelationInstance();

        relationInstance.setId(String.valueOf((node.getCertname() + master.getId()).hashCode()));
        relationInstance.setTargetInstanceId(master.getId());
        relationInstance.setType(RelationTypes.RelationType.connectsTo);

        return Collections.singletonList(relationInstance);

    }
}

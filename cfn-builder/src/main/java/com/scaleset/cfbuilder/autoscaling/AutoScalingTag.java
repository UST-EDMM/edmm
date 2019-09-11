package com.scaleset.cfbuilder.autoscaling;

import com.scaleset.cfbuilder.core.Tag;

public class AutoScalingTag extends Tag {

    public AutoScalingTag(String key, String value) {
        super(key, value);
    }

    public AutoScalingTag(String key, String value, boolean propagateAtLaunch) {
        super(key, value);
        node.put("PropagateAtLaunch", true);
    }

    public Boolean getPropagateAtLaunch() {
        return node.get("PropagateAtLaunch").asBoolean(false);
    }
}

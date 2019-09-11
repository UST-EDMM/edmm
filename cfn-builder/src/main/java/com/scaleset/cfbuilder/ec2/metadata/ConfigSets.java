package com.scaleset.cfbuilder.ec2.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigSets {

    public String id = "configSets";

    public Map<String, List<String>> sets;

    public ConfigSets(String configSet) {
        this.sets = new HashMap<>();
        this.sets.put(configSet, new ArrayList<>());
    }

    public void addConfig(String configSet, String config) {
        if (this.sets.containsKey(configSet)) {
            //Add String to configset list
            this.sets.get(configSet).add(config);
        } else {
            throw new IllegalArgumentException("Config set " + configSet + "does not exist");
        }
    }

    public Map<String, List<String>> getSets() {
        return this.sets;
    }

    public String getId() {
        return this.id;
    }
}

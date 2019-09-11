package com.scaleset.cfbuilder.ec2.metadata;

import java.util.HashMap;
import java.util.Map;

public class CFNInit {

    public Map<String, Config> configs;

    public ConfigSets configSets;

    /**
     * Add the configset with a list
     */
    public CFNInit(String configSet) {
        this.configs = new HashMap<>();
        this.configSets = new ConfigSets(configSet);
    }

    /**
     * Add the config to the configset if the configset exists
     */
    public CFNInit addConfig(String configSet, Config config) {
        this.configSets.addConfig(configSet, config.getId());
        this.configs.put(config.getId(), config);
        return this;
    }

    public Map<String, Config> getConfigs() {
        return this.configs;
    }

    public Config getConfig(String config) {
        return this.configs.get(config);
    }

    public Config getOrAddConfig(String configSet, String config) {
        if (this.configs.get(config) != null) {
            return this.configs.get(config);
        } else {
            Config newConfig = new Config(config);
            addConfig(configSet, newConfig);
            return newConfig;
        }
    }

    public ConfigSets getConfigSets() {
        return this.configSets;
    }
}

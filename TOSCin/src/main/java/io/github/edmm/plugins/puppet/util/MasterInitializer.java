package io.github.edmm.plugins.puppet.util;

import java.util.Map;

import io.github.edmm.plugins.puppet.model.Master;
import io.github.edmm.util.CastUtil;

public class MasterInitializer {
    private Master master;

    public MasterInitializer(Master master) {
        this.master = master;
    }

    public void setupMaster() {
        this.setMasterHostName();
        this.setMasterId();
        this.setPuppetVersion();
        this.setCreatedAtTimestamp();
    }

    private void setMasterHostName() {
        this.master.setHostName(this.buildMasterNameFromString(this.master.executeCommandAndHandleResult(Commands.GET_MASTER)));
    }

    private void setMasterId() {
        this.master.setId(String.valueOf((this.master.getHostName() + this.master.getIp()).hashCode()));
    }

    private void setPuppetVersion() {
        this.master.setPuppetVersion(this.master.executeCommandAndHandleResult(Commands.GET_VERSION));
    }

    private void setCreatedAtTimestamp() {
        this.master.setCreatedAtTimestamp(this.master.executeCommandAndHandleResult(Commands.GET_CREATED_AT_TIMESTAMP));
    }

    private String buildMasterNameFromString(String jsonString) {
        Map<String, String> masterNameKeyValuePair = CastUtil.safelyCastToStringStringMap(GsonHelper.parseJsonStringToObjectType(
            jsonString.substring(1, jsonString.length() - 1),
            Map.class));
        return masterNameKeyValuePair.get("name");
    }
}

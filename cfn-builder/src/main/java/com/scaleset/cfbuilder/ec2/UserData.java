package com.scaleset.cfbuilder.ec2;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scaleset.cfbuilder.core.Fn;

/**
 * Constructs a {@code UserData}, a Base64-encoded MIME user data that is made available to the EC2 {@link Instance}.
 */
public class UserData {

    @JsonIgnore
    private Map<String, Fn> dataMap;

    public UserData(Fn joinFn) {
        dataMap = new HashMap<>();
        dataMap.put("Fn::Base64", joinFn);
    }

    @JsonAnyGetter
    public Map<String, Fn> getDataMap() {
        return this.dataMap;
    }
}

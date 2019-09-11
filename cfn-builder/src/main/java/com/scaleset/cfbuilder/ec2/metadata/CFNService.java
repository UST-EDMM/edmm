package com.scaleset.cfbuilder.ec2.metadata;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CFNService {

    @JsonIgnore
    public String id;

    private Map<String, SimpleService> services;

    public CFNService() {
        //Hardcoded because we only support sysvinit atm
        this.id = "sysvinit";
        this.services = new HashMap<>();
    }

    public CFNService(String id) {
        this();
    }

    public String getId() {
        return this.id;
    }

    public CFNService addService(SimpleService service) {
        this.services.put(service.getId(), service);
        return this;
    }

    @JsonAnyGetter
    public Map<String, SimpleService> getServices() {
        return this.services;
    }
}

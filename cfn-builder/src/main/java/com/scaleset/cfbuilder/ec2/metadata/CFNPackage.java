package com.scaleset.cfbuilder.ec2.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

public class CFNPackage {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, List<String>> packages;

    @JsonIgnore
    public String id;

    public CFNPackage(String id) {
        this.id = id;
        this.packages = new HashMap<>();
    }

    public String getId() {
        return this.id;
    }

    public CFNPackage addPackage(String packageName) {
        this.packages.put(packageName, new ArrayList<>());
        return this;
    }

    public CFNPackage addPackage(String packageName, String version) {
        ArrayList<String> list = new ArrayList<>();
        list.add(version);
        this.packages.put(packageName, list);
        return this;
    }

    @JsonAnyGetter
    public Map<String, List<String>> getPackages() {
        return this.packages;
    }
}

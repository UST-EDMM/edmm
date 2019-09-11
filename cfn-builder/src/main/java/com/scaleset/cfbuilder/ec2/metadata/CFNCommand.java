package com.scaleset.cfbuilder.ec2.metadata;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder( {"command", "env", "cwd"})
public class CFNCommand {

    @JsonProperty("command")
    public String command;

    @JsonProperty("env")
    public Map<String, Object> env;

    @JsonProperty("cwd")
    public String cwd;

    //TODO optionals: test, ignoreErrors

    @JsonIgnore
    public String id;

    public CFNCommand(String id, String command) {
        this.id = id;
        this.command = command;
        this.env = new HashMap<>();
    }

    public String getId() {
        return this.id;
    }

    public CFNCommand addEnv(String envVar, Object value) {
        this.env.put(envVar, value);
        return this;
    }

    public CFNCommand setCwd(String cwd) {
        this.cwd = cwd;
        return this;
    }
}

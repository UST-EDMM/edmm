package com.scaleset.cfbuilder.ec2.metadata;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder( {"packages", "files", "commands", "services"})
public class Config {

    @JsonProperty("packages")
    public Map<String, CFNPackage> packages;

    @JsonProperty("files")
    public Map<String, CFNFile> files;

    @JsonProperty("commands")
    public Map<String, CFNCommand> commands;

    @JsonProperty("services")
    public Map<String, CFNService> services;

    //TODO add: groups, users, sources, services

    @JsonIgnore
    public String id;

    public Config(String id) {
        this.id = id;
        this.packages = new HashMap<>();
        this.files = new HashMap<>();
        this.commands = new HashMap<>();
        this.services = new HashMap<>();
    }

    public Config putCommand(CFNCommand command) {
        this.commands.put(command.getId(), command);
        return this;
    }

    public Config putPackage(CFNPackage cfnPackage) {
        this.packages.put(cfnPackage.getId(), cfnPackage);
        return this;
    }

    public Config putFile(CFNFile file) {
        this.files.put(file.getId(), file);
        return this;
    }

    public Config putService(CFNService service) {
        this.services.put(service.getId(), service);
        return this;
    }

    public String getId() {
        return this.id;
    }
}

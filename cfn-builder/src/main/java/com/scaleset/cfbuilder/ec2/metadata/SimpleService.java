package com.scaleset.cfbuilder.ec2.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleService {

    @JsonIgnore
    private String id;

    private Boolean ensureRunning;
    private Boolean enabled;
    private List<String> files;
    private List<String> sources;
    private List<String> commands;
    private Map<String, List<String>> packages;

    public SimpleService(String id) {
        this.id = id;
        this.files = new ArrayList<>();
        this.sources = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.packages = new HashMap<>();
    }

    public String getId() {
        return this.id;
    }

    public SimpleService setEnsureRunning(Boolean ensureRunning) {
        this.ensureRunning = ensureRunning;
        return this;
    }

    public SimpleService setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public SimpleService addFile(String file) {
        this.files.add(file);
        return this;
    }

    public SimpleService addSource(String source) {
        this.sources.add(source);
        return this;
    }

    public SimpleService addCommand(String command) {
        this.commands.add(command);
        return this;
    }

    public SimpleService addPackage(String packageFormat, String... packages) {
        this.packages.put(packageFormat, Arrays.asList(packages));
        return this;
    }

    @JsonProperty("ensureRunning")
    public Boolean getEnsureRunning() {
        return ensureRunning;
    }

    @JsonProperty("enabled")
    public Boolean getEnabled() {
        return enabled;
    }

    @JsonProperty("files")
    public List<String> getFiles() {
        return files;
    }

    @JsonProperty("sources")
    public List<String> getSources() {
        return sources;
    }

    @JsonProperty("commands")
    public List<String> getCommands() {
        return commands;
    }

    @JsonProperty("packages")
    public Map<String, List<String>> getPackages() {
        return packages;
    }
}

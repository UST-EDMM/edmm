package io.github.edmm.plugins.cloudify.model.azure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

public class Operation {

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private String source;

    @Getter
    private List<Script> scripts = new ArrayList<>();

    public Operation(String name, String source) {
        this.name = name;
        this.source = source;
    }

    public void addScript(String name, String path) {
        List<String> previous = scripts.stream().map(Script::getName).collect(Collectors.toList());
        Script result = Script.builder().name(name).path(path).previous(previous).build();
        scripts.add(result);
    }
}

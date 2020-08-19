package io.github.edmm.exporter.dto;

public class InstanceDTO {
    String name;
    String type;
    String required;

    public InstanceDTO(String name, String type, String required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }
}

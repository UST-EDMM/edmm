package io.github.edmm.exporter.dto;

public class ServiceTemplateCreationDTO {
    String namespace;
    String localname;

    public ServiceTemplateCreationDTO(String namespace, String localname) {
        this.namespace = namespace;
        this.localname = localname;
    }
}

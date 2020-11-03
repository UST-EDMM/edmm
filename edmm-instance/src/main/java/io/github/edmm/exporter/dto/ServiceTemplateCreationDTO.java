package io.github.edmm.exporter.dto;

public class ServiceTemplateCreationDTO {
    private String namespace;
    private String localname;

    public ServiceTemplateCreationDTO(String namespace, String localname) {
        this.namespace = namespace;
        this.localname = localname;
    }
}

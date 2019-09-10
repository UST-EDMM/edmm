package io.github.edmm.plugins.chef.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Metadata {
    private String name;
    private String maintainer;
    private String maintainerEmail;
    private String license;
    private String description;
    private String longDescription;
    private String version;
    private List<String> supportedOS;
    private String url;
}
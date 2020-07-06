package io.github.edmm.plugins.puppet.model;

import lombok.Getter;

@Getter
public class ResourceEventEntry {
    String status;
    ResourceType resource_type;
    String resource_title;
}

package io.github.edmm.plugins.puppet.model;

import java.util.List;

import lombok.Getter;

@Getter
public class ResourceEvent {
    List<ResourceEventEntry> data;
}

package io.github.edmm.plugins.puppet.model;

import lombok.Getter;

@Getter
public class Report {
    String certname;
    ResourceEvent resource_events;
}

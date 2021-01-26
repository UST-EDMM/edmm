package io.github.edmm.plugins.puppet.model;

import lombok.Getter;

@Getter
public class Report {

    private String certname;
    private ResourceEvent resource_events;
    private State status;
    private String environment;

    public enum State {
        unchanged,
        changed,
        failed,
    }
}

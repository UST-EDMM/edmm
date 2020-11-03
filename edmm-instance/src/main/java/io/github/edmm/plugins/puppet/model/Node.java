package io.github.edmm.plugins.puppet.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node {
    private String certname;
    private String latest_report_hash;
    private String latest_report_status;
    private List<Fact> facts;
    private PuppetState.NodeState state;
}

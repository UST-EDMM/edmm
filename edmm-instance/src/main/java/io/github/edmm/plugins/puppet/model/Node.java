package io.github.edmm.plugins.puppet.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node {
    private Report report;
    private String certname;
    private String latest_report_hash;
    private String facts_timestamp;
    private List<Fact> facts;
}

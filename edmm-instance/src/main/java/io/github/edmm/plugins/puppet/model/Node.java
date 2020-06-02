package io.github.edmm.plugins.puppet.model;

import lombok.Getter;

@Getter
public class Node {
    private Report report;
    private String certname;
    private String latest_report_hash;
    private String facts_timestamp;
    private Fact fact;
}

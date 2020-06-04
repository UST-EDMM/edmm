package io.github.edmm.plugins.puppet.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node {
    private String certname;
    private String latest_report_hash;
    private List<Fact> facts;
}

package io.github.edmm.plugins.puppet.model;

import lombok.Getter;

@Getter
class Fact {
    private String certname;
    private String name;
    private String value;
}

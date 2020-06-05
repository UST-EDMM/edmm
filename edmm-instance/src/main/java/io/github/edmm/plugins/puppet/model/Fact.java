package io.github.edmm.plugins.puppet.model;

import lombok.Getter;

@Getter
public class Fact {
    private String certname;
    private String name;
    private String value;

    Fact(String certname, String name, String value) {
        this.certname = certname;
        this.name = name;
        this.value = value;
    }
}

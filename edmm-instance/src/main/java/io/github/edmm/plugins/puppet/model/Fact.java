package io.github.edmm.plugins.puppet.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Fact {
    private String certname;
    @Setter
    private String name;
    private String value;

    public Fact(String certname, String name, String value) {
        this.certname = certname;
        this.name = name;
        this.value = value;
    }
}

package io.github.edmm.plugins.puppet.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Task {
    private String name;
    private String scriptFileName;
    private String varString;
}

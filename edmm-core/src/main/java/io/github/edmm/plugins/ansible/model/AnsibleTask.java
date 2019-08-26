package io.github.edmm.plugins.ansible.model;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class AnsibleTask {
    private String name;
    private String script;
    private Map<String, String> args;
}

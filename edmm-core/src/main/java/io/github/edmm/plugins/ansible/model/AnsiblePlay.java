package io.github.edmm.plugins.ansible.model;

import java.util.Map;
import java.util.Queue;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class AnsiblePlay {
    private String name;
    private String hosts;
    private boolean become;
    private String becomeUser;
    private Map<String, String> vars;
    private Queue<AnsibleTask> tasks;


}

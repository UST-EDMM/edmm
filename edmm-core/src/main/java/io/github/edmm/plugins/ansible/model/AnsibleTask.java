package io.github.edmm.plugins.ansible.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class AnsibleTask {
    private String name;

}

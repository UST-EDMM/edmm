package io.github.edmm.plugins.ansible.model;

import lombok.Value;

@Value
public class AnsibleFile {
    String src;
    String target;
}

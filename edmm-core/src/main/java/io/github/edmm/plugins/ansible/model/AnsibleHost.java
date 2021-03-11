package io.github.edmm.plugins.ansible.model;

import lombok.Value;

@Value
public class AnsibleHost {
    String name;
    String privateKeyFile;
}

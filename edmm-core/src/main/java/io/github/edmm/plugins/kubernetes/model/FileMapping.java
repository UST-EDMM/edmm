package io.github.edmm.plugins.kubernetes.model;

import lombok.Data;

@Data
public class FileMapping {
    private String source;
    private String target;
}

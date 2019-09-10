package io.github.edmm.plugins.puppet.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Task {
    private String name;
    private String scriptFileName;
    private List<String> envVars;
}

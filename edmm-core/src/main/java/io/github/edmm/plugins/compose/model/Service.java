package io.github.edmm.plugins.compose.model;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Service {

    private String name;
    private String targetDirectory;
    private List<Integer> ports;
    private Map<String, String> envVars;
}

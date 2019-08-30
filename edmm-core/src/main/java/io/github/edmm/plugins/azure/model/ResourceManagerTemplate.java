package io.github.edmm.plugins.azure.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.edmm.plugins.azure.model.resource.Resource;
import lombok.Data;

@Data
public class ResourceManagerTemplate {
    private final String $schema = "https://schema.management.azure.com/schemas/2015-01-01/deploymentTemplate.json#";
    private String contentVersion = "1.0.0.0";
    private Map<String, Parameter> parameters = new HashMap<>();
    private Map<String, String> variables = new HashMap<>();
    private List<Resource> resources = new ArrayList<>();
}
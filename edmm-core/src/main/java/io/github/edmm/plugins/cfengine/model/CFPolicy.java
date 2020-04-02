package io.github.edmm.plugins.cfengine.model;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * Abstraction of a policy
 */
@Data
@Builder
public class CFPolicy {
    public final Map<String, String> modVars;
    public final Map<String, Map<String, String>> envVars;
    public final Map<String, String> classes;
    public final Map<String, List<String>> methods;
}

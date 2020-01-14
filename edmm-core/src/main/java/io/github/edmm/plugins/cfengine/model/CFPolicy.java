package io.github.edmm.plugins.cfengine.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
/**
 * Abstraction of a policy
 */
public class CFPolicy {
    public final Map<String, String> modVars;
    public final Map<String, Map<String, String>> envVars;
    public final Map<String, String> classes;
    public final Map<String, List<String>> methods;

}

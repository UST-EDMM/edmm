package io.github.edmm.plugins.cfengine.model;

import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Data
@Builder
/**
 * Abstraction of a policy
 */
public class CFPolicy {
    private static final Logger logger = LoggerFactory.getLogger(CFPolicy.class);
    public final Map<String, String> modVars;
    public final Map<String, List<String>> envVars;
    public final Map<String, String> classes;
    public final Map<String, List<String>> methods;

}

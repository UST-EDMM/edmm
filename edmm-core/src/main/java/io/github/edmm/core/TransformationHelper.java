package io.github.edmm.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.edmm.model.component.MongoDb;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;

import lombok.var;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;

import static io.github.edmm.model.component.Dbms.PORT;

public abstract class TransformationHelper {

    private static final String[] DEFAULT_PROPERTY_BLACKLIST = {"*key_name*", "*public_key*", "name"};

    public static boolean matchesBlacklist(String name, String... blacklist) {
        Set<String> values = Stream.concat(Arrays.stream(DEFAULT_PROPERTY_BLACKLIST), Arrays.stream(blacklist))
            .collect(Collectors.toSet());
        for (String s : values) {
            if (FilenameUtils.wildcardMatch(name, s)) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, String> collectEnvVars(Graph<RootComponent, RootRelation> graph, RootComponent component) {
        Map<String, String> envVars = new HashMap<>();
        var properties = TopologyGraphHelper.resolveComponentStackProperties(graph, component);
        for (var entry : properties.entrySet()) {
            var p = entry.getValue();
            if (matchesBlacklist(entry.getKey())) {
                continue;
            }
            if (p.isComputed() || p.getValue() == null || p.getValue().startsWith("$")) {
                continue;
            }
            envVars.put(entry.getKey().toUpperCase(), p.getValue());
        }
        return envVars;
    }

    public static Map<String, String> collectEnvVars(RootComponent component) {
        Map<String, String> envVars = new HashMap<>();
        component.getProperties().values().stream()
            .filter(p -> !matchesBlacklist(p.getName()))
            .filter(p -> !(p.isComputed() || p.getValue() == null || p.getValue().startsWith("$")))
            .forEach(p -> envVars.put(p.getNormalizedName().toUpperCase(), StringUtils.isBlank(p.getValue()) ? "\"\"" : p.getValue()));

        if (component instanceof MongoDb) {
            envVars.put(PORT.getName().toUpperCase(), "27017");
        }

        return envVars;
    }

    public static List<String> collectRuntimeEnvVars(Graph<RootComponent, RootRelation> graph, RootComponent component) {
        List<String> envVars = new ArrayList<>();
        var allProps = TopologyGraphHelper.resolveComponentStackProperties(graph, component);
        for (var entry : allProps.entrySet()) {
            var p = entry.getValue();
            if (matchesBlacklist(entry.getKey())) {
                continue;
            }
            if (p.isComputed() || p.getValue() == null || p.getValue().startsWith("$")) {
                envVars.add(entry.getKey().toUpperCase());
            }
        }
        return envVars;
    }
}

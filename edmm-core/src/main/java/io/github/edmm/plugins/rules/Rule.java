package io.github.edmm.plugins.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.support.EdmmYamlBuilder;

import lombok.Getter;

public abstract class Rule implements Comparable<Rule> {
    @Getter
    protected String name;

    @Getter
    protected String description;

    @Getter
    protected Integer priority;

    @Getter
    protected ReplacementReason reason;

    private final RuleAssessor ruleAssessor;

    public Rule(String name, String description, int priority, ReplacementReason reason) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.reason = reason;

        ruleAssessor = new RuleAssessor();
    }

    public Rule(String name, String description) {
        this(name,description,Integer.MAX_VALUE - 1, ReplacementReason.UNSUPPORTED);
    }

    public boolean evaluate(DeploymentModel actualModel,RootComponent unsupportedComponent) {
        DeploymentModel expectedModel = DeploymentModel.of(fromTopology(new EdmmYamlBuilder()).build());

        boolean topologyMatches = ruleAssessor.assess(expectedModel,actualModel,unsupportedComponent,false);

        List<EdmmYamlBuilder> exceptYamlBuilders = this.exceptTopologies(new ArrayList<>());

        if (exceptYamlBuilders != null && topologyMatches) {
            // if the topology matches we check for exceptions
            for (EdmmYamlBuilder yamlBuilder: exceptYamlBuilders) {
                 DeploymentModel exceptionModel = DeploymentModel.of(yamlBuilder.build());
                 if ( ruleAssessor.assess(exceptionModel, actualModel, unsupportedComponent, true) ) {
                     // if there is an exact match this rule shouldn't be evaluated because
                     // we found a topology representing an exception
                     return false;
                 }
            }
        }
        return topologyMatches;
    }

    public Rule.Result execute() {
        EdmmYamlBuilder yamlBuilderFrom = new EdmmYamlBuilder();
        EdmmYamlBuilder yamlBuilderTo = new EdmmYamlBuilder();

        return new Rule.Result(
            this.reason,
            fromTopology(yamlBuilderFrom).getComponentsMap(),
            toTopology(yamlBuilderTo).getComponentsMap());
    }

    protected abstract EdmmYamlBuilder fromTopology(EdmmYamlBuilder yamlBuilder);

    protected abstract EdmmYamlBuilder toTopology(EdmmYamlBuilder yamlBuilder);

    /**
     * @return a list of topologies that are exceptions w.r.t. fromTopology
     *          i.e. the plugin supports Auth0 but not any other Saas: fromTopology function will return Saas,
     *          while this function will return Auth0, so that this rule will match every Saas except Auth0
     */
    protected List<EdmmYamlBuilder> exceptTopologies(List<EdmmYamlBuilder> yamlBuilders) {
        return null;
    }

    public static List<Rule> getDefault() {
        List<Rule> rules = new ArrayList<>();
        rules.add(new PaasDefaultRule());
        rules.add(new SaasDefaultRule());
        rules.add(new DbaasDefaultRule());
        return  rules;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Rule rule) {
        return getPriority().compareTo(rule.getPriority());
    }

    public enum ReplacementReason {
        UNSUPPORTED("unsupported"), // the fromTopology is completely unsupported
        PARTLY_SUPPORTED("partlySupported"), // the fromTopology is partly supported
        PREFERRED("preferred"); // the fromTopology is supported but we suggest the toTopology as replacement

        private final String label;

        ReplacementReason(String reason) {
            this.label = reason;
        }
    }

    @Getter
    public static class Result {
        private final String reason;
        private final Map<String,Object> fromTopology;
        private final Map<String,Object> toTopology;

        public Result(ReplacementReason reason, Map<String,Object> from, Map<String,Object> to) {
            this.reason = reason.label;
            fromTopology = from;
            toTopology = to;
        }
    }
}

package io.github.edmm.plugins.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.support.BaseElement;
import io.github.edmm.model.support.EdmmYamlBuilder;

import lombok.Getter;

public abstract class Rule implements Comparable<Rule> {
    public static int DEFAULT_PRIORITY = Integer.MAX_VALUE - 1;

    @Getter
    protected String name;

    @Getter
    protected String description;

    @Getter
    protected Integer priority;

    @Getter
    protected ReplacementReason reason;

    private final RuleAssessor ruleAssessor;
    private List<RootComponent> unsupportedComponents = null;

    /**
     * @param priority the order of evaluation/execution of the rule
     */
    public Rule(String name, String description, int priority, ReplacementReason reason) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.reason = reason;

        ruleAssessor = new RuleAssessor();
    }

    public Rule(String name, String description, ReplacementReason reason) {
        this(name, description, DEFAULT_PRIORITY, reason);
    }

    public Rule(String name, String description) {
        this(name, description, DEFAULT_PRIORITY, ReplacementReason.UNSUPPORTED);
    }

    /**
     * @param currentComponent the component currently visited
     */
    public boolean evaluate(DeploymentModel actualModel, RootComponent currentComponent) {
        DeploymentModel expectedModel = DeploymentModel.of(fromTopology(new EdmmYamlBuilder()).build());

        RuleAssessor.Result assessResult = ruleAssessor.assess(expectedModel, actualModel, currentComponent, false);
        // the unsupportedComponents should be retrieved immediately after the asses function
        // if evaluate returns true its value is valid and will be used in the execute function
        unsupportedComponents = assessResult.getUnsupportedComponents();

        EdmmYamlBuilder[] exceptYamlBuilders = this.exceptTopologies();

        if (exceptYamlBuilders != null && assessResult.matches()) {
            // if the topology matches we check for exceptions
            for (EdmmYamlBuilder yamlBuilder : exceptYamlBuilders) {
                DeploymentModel exceptionModel = DeploymentModel.of(yamlBuilder.build());
                if (ruleAssessor.assess(exceptionModel, actualModel, currentComponent, true).matches()) {
                    // if there is an exact match this rule shouldn't be evaluated because
                    // we found a topology representing an exception
                    return false;
                }
            }
        }
        return assessResult.matches();
    }

    public Rule.Result execute() throws NullPointerException {
        if (unsupportedComponents == null) {
            throw new NullPointerException("Rule must be evaluated before getting executed");
        }

        return new Rule.Result(
            this.reason,
            this.unsupportedComponents,
            toTopology(new EdmmYamlBuilder()).getComponentsMap());
    }

    protected abstract EdmmYamlBuilder fromTopology(EdmmYamlBuilder yamlBuilder);

    protected abstract EdmmYamlBuilder toTopology(EdmmYamlBuilder yamlBuilder);

    /**
     * @return an array of topologies that are exceptions w.r.t. fromTopology i.e. the plugin supports Auth0 but not any
     * other Saas: fromTopology function will return Saas, while this function will return Auth0, so that this rule will
     * match every Saas except Auth0
     */
    protected EdmmYamlBuilder[] exceptTopologies() {
        return null;
    }

    public static List<Rule> getDefault() {
        List<Rule> rules = new ArrayList<>();
        rules.add(new PaasDefaultRule());
        rules.add(new SaasDefaultRule());
        rules.add(new DbaasDefaultRule());
        return rules;
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

        @Override
        public String toString() {
            return this.label;
        }
    }

    @Getter
    public class Result {
        private final String reason;
        private final List<String> unsupportedComponents;
        private final Map<String, Object> toTopology;

        public Result(ReplacementReason reason, List<RootComponent> unsupportedComponents, Map<String, Object> toTopology) {
            this.reason = reason.toString();
            this.toTopology = toTopology;
            this.unsupportedComponents = unsupportedComponents.stream()
                .map(BaseElement::getId)
                .collect(Collectors.toList());
        }

        public boolean isUnsupported() {
            return !this.reason.equals(ReplacementReason.PREFERRED.toString());
        }

        @Override
        public boolean equals(Object r) {
            if (this == r) return true;
            if (r == null || getClass() != r.getClass()) return false;
            Result result = (Result) r;
            return this.reason.equals(result.reason) &&
                this.unsupportedComponents.equals(result.unsupportedComponents) &&
                this.toTopology.equals(result.toTopology);
        }
    }
}

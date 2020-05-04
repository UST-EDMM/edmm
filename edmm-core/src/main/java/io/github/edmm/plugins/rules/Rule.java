package io.github.edmm.plugins.rules;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;

import lombok.Getter;

public abstract class Rule implements Comparable<Rule> {
    @Getter
    protected String name;

    @Getter
    protected String description;

    @Getter
    protected Integer priority;

    public Rule(String name, String description, int priority) {
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    public Rule(String name, String description) {
        this(name,description,Integer.MAX_VALUE - 1);
    }

    public boolean evaluate(DeploymentModel actualModel,RootComponent unsupportedComponent) {

        DeploymentModel expectedModel = DeploymentModel.of(fromTopology());
        RuleAssessor ruleAssessor = new RuleAssessor(expectedModel,actualModel);
        return ruleAssessor.assess(unsupportedComponent);
    }

    protected abstract String fromTopology();

    protected abstract String toTopology();

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Rule rule) {
        return getPriority().compareTo(rule.getPriority());
    }
}

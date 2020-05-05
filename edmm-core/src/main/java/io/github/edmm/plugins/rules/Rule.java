package io.github.edmm.plugins.rules;

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

    public Rule(String name, String description, int priority) {
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    public Rule(String name, String description) {
        this(name,description,Integer.MAX_VALUE - 1);
    }

    public boolean evaluate(DeploymentModel actualModel,RootComponent unsupportedComponent) {
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        DeploymentModel expectedModel = DeploymentModel.of(fromTopology(yamlBuilder).build());

        RuleAssessor ruleAssessor = new RuleAssessor(expectedModel,actualModel);
        return ruleAssessor.assess(unsupportedComponent);
    }

    protected abstract EdmmYamlBuilder fromTopology(EdmmYamlBuilder yamlBuilder);

    protected abstract EdmmYamlBuilder toTopology(EdmmYamlBuilder yamlBuilder);

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Rule rule) {
        return getPriority().compareTo(rule.getPriority());
    }
}

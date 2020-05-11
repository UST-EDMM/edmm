package io.github.edmm.plugins.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuleEngine {

    /**
     * @param rules the plugin specific rules. The function will add the default rules and sort them all.
     */
    public static List<Rule.Result> fire(DeploymentModel model, @NotNull List<Rule> rules, RootComponent unsupportedComponent) {
        List<Rule.Result> results = new ArrayList<>();

        // we always add the default rules
        rules.addAll(Rule.getDefault());
        // the rules are sorted by their priority
        Collections.sort(rules);

        log.debug("Rules evaluation started");
        for (Rule rule : rules) {
            String name = rule.getName();
            boolean evaluationResult = false;
            try {
                evaluationResult = rule.evaluate(model,unsupportedComponent);
            } catch (IllegalArgumentException | NullPointerException e) {
                log.error("Rule '" + name + "' evaluated with error", e);
            }
            if (evaluationResult) {
                log.debug("Rule '{}' triggered", name);
                Rule.Result result = rule.execute(unsupportedComponent);
                results.add(result);
            } else {
                log.debug("Rule '{}' has been evaluated to false, it has not been executed", name);
            }
        }

        return results;
    }
}

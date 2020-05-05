package io.github.edmm.plugins.rules;

import java.util.List;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuleEngine {

    public void fire(DeploymentModel model, List<Rule> rules, RootComponent unsupportedComponent) {
        if (rules.isEmpty()) {
            log.warn("No rules registered! Nothing to apply");
            return;
        }
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
                rule.execute();

            } else {
                log.debug("Rule '{}' has been evaluated to false, it has not been executed", name);
            }
        }
    }
}

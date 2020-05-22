package io.github.edmm.plugins.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuleEngine {
    @Getter
    private final List<Rule.Result> results; // the results will follow the rule priority order

    public RuleEngine() {
        results = new ArrayList<>();
    }

    public void fire(DeploymentModel model, List<Rule> rules, RootComponent unsupportedComponent ) {
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
                Rule.Result result = rule.execute();

                if (!results.contains(result)) {
                    // we do not want duplicates
                    results.add(result);
                }
            } else {
                log.debug("Rule '{}' has been evaluated to false, it has not been executed", name);
            }
        }
    }

    public void fire(TransformationContext context, TransformationPlugin<?> plugin) {
        DeploymentModel model = context.getModel();

        for (RootComponent component : model.getComponents()) {
            this.fire(model, plugin.getRules(), component);
        }
    }

    /**
     * @return the number of rule results that has UNSUPPORTED or PARTLY_SUPPORTED has reason field value
     */
    public long getUnsupportedRulesCount() {
        return results.stream()
            .filter(result -> !result.getReason().equals(Rule.ReplacementReason.PREFERRED.toString()))
            .count();
    }
}

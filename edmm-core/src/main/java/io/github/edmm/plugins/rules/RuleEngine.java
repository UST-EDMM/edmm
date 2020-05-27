package io.github.edmm.plugins.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuleEngine {
    private final List<Rule.Result> results; // the results will follow the rule priority order

    public RuleEngine() {
        results = new ArrayList<>();
    }

    public List<Rule.Result> fire(DeploymentModel model, @NonNull List<Rule> rules, RootComponent unsupportedComponent ) {
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
                try {
                    Rule.Result result = rule.execute();
                    // we do not want duplicates
                    if (!results.contains(result)) { results.add(result); }
                } catch (NullPointerException e) {
                    log.error("Rule '" + name + "' executed with error", e);
                }
            } else {
                log.debug("Rule '{}' has been evaluated to false, it has not been executed", name);
            }
        }

        return results;
    }

    public List<Rule.Result> fire(TransformationContext context, TransformationPlugin<?> plugin) {
        DeploymentModel model = context.getModel();

        for (RootComponent component : model.getComponents()) {
            this.fire(model, plugin.getRules(), component);
        }

        return results;
    }

    /**
     * @return the number of rule results that has UNSUPPORTED or PARTLY_SUPPORTED as reason field value
     */
    public static long countUnsupportedRules(List<Rule.Result> resultList) {
        return resultList.stream()
            .filter(Rule.Result::isUnsupported)
            .count();
    }
}

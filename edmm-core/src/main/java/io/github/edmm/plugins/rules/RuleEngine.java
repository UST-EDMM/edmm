package io.github.edmm.plugins.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuleEngine {
    @Getter
    private final Map<String, List<Rule.Result>> results;

    public RuleEngine() {
        results = new HashMap<>();
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
                this.put(unsupportedComponent,result);
            } else {
                log.debug("Rule '{}' has been evaluated to false, it has not been executed", name);
            }
        }
    }

    public void fire(TransformationContext context, Plugin<?> plugin) {
        DeploymentModel model = context.getModel();

        for (RootComponent component : model.getComponents()) {
            this.fire(model, plugin.getRules(), component);
        }
    }

    private void put(RootComponent unsupportedComponent, Rule.Result result) {
        List<Rule.Result> ruleResults = results.get(unsupportedComponent.getName());
        if (ruleResults == null) {
          ruleResults = new ArrayList<>();
        }
        ruleResults.add(result);
        results.put(unsupportedComponent.getName(), ruleResults);
    }

}

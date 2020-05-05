package io.github.edmm.plugins.compose.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.TemplateHelper;
import io.github.edmm.core.TopologyGraphHelper;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.docker.Container;
import io.github.edmm.docker.DependencyGraph;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.plugins.compose.DockerComposePlugin;
import io.github.edmm.plugins.compose.model.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerComposeBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DockerComposeBuilder.class);

    private final List<Container> containers;
    private final DependencyGraph dependencyGraph;
    private final PluginFileAccess fileAccess;

    private final Configuration cfg = TemplateHelper.forClasspath(DockerComposePlugin.class, "/plugins/compose");

    public DockerComposeBuilder(List<Container> containers, DependencyGraph dependencyGraph, PluginFileAccess fileAccess) {
        this.containers = containers;
        this.dependencyGraph = dependencyGraph;
        this.fileAccess = fileAccess;
    }

    public void populateComposeFile() {
        List<Service> services = new ArrayList<>();
        Template template = TemplateHelper.getTemplate(cfg, "docker-compose.yml");
        for (Container stack : containers) {
            List<String> dependencies = new ArrayList<>();
            resolveDependencies(stack, dependencies);
            services.add(new Service(stack, dependencies));
        }
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("services", services);
            String fileContent = TemplateHelper.toString(template, data);
            fileAccess.append("/docker-compose.yml", fileContent);
        } catch (Exception e) {
            logger.error("Failed to create Docker Compose file", e);
            throw new TransformationException(e);
        }
    }

    private void resolveDependencies(Container stack, List<String> dependencies) {
        Set<Container> targetStacks = TopologyGraphHelper.getTargetComponents(dependencyGraph, stack, ConnectsTo.class);
        for (Container target : targetStacks) {
            for (Map.Entry<String, String> envVar : target.getEnvVars().entrySet()) {
                stack.addEnvVar(envVar.getKey(), envVar.getValue());
            }
            dependencies.add(target.getLabel());
            stack.addEnvVar((target.getName() + "_HOSTNAME").toUpperCase(), target.getLabel());
        }
    }
}

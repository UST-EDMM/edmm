package io.github.edmm.cli;

import java.util.UUID;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.plugins.kubernetes.KubernetesInstancePlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "kubernetes", descriptionHeading = "%n", description = "Starts a transformation from Kubernetes to OpenTOSCA.", customSynopsis = "@|bold edmmi transform_puppet|@ @|yellow <path to edmmi yaml file>|@")
public class KubernetesCommand extends TransformCommand {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesCommand.class);
    private static final SourceTechnology KUBERNETES = SourceTechnology.builder()
        .id("kubernetes")
        .name("Kubernetes")
        .build();

    @CommandLine.Option(names = {"-c", "--kubeConfigPath"}, required = true)
    private String kubeConfigPath;
    @CommandLine.Option(names = {"--namespace"}, description = "You mayspecify the target namespace of the application you want to transform.")
    private String targetNamespace;

    @Override
    public void run() {
        InstanceTransformationContext context = new InstanceTransformationContext(UUID.randomUUID().toString(),
            KUBERNETES,
            outputPath);
        KubernetesInstancePlugin pluginLifecycle = new KubernetesInstancePlugin(context, kubeConfigPath, targetNamespace);
        InstancePlugin<KubernetesInstancePlugin> plugin = new InstancePlugin<>(KUBERNETES, pluginLifecycle);
        try {
            plugin.execute();
        } catch (Exception e) {
            logger.error("Error while executing transformation.", e);
        }
    }
}

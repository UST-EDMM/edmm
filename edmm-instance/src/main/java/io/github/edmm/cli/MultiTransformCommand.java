package io.github.edmm.cli;

import java.util.UUID;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.plugins.kubernetes.KubernetesInstancePlugin;
import io.github.edmm.plugins.puppet.PuppetInstancePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(
    name = "multitransform",
    descriptionHeading = "%n",
    description = "Starts a transformation from multiple source technologies to OpenTOSCA.",
    customSynopsis = "@|bold edmmi transform_puppet|@ @|yellow <path to edmmi yaml file>|@"
)
public class MultiTransformCommand extends TransformCommand {
    private static final Logger logger = LoggerFactory.getLogger(MultiTransformCommand.class);
    private static final SourceTechnology MULTI_TRANSFORM = SourceTechnology.builder().id("multitransform").name("Multi Transform").build();

    @CommandLine.Option(names = {"-c", "--kubeConfigPath"}, required = true)
    private String kubeConfigPath;
    @CommandLine.Option(names = {"-u", "--user"}, required = true)
    private String user;
    @CommandLine.Option(names = {"-ip", "--masterIp"}, required = true)
    private String ip;
    @CommandLine.Option(names = {"-f", "--privateKeyFileLocation"}, required = true)
    private String privateKeyLocation;
    @CommandLine.Option(names = {"-p", "--port"}, defaultValue = "22")
    private Integer port;

    @Override
    public void run() {
        TOSCATransformer toscaTransformer = new TOSCATransformer();
        InstanceTransformationContext context = new InstanceTransformationContext(UUID.randomUUID().toString(), MULTI_TRANSFORM, outputPath, true);
        KubernetesInstancePlugin kubernetesLifecycle = new KubernetesInstancePlugin(context, kubeConfigPath, null);
        InstancePlugin<KubernetesInstancePlugin> kubernetesPlugin = new InstancePlugin<>(MULTI_TRANSFORM, kubernetesLifecycle);
        try {
            kubernetesPlugin.execute();
        } catch (Exception e) {
            logger.error("Error while executing transformation.", e);
        }
        PuppetInstancePlugin puppetLifecycle = new PuppetInstancePlugin(context, user, ip, privateKeyLocation, port, null, null);
        InstancePlugin<PuppetInstancePlugin> puppetPlugin = new InstancePlugin<>(MULTI_TRANSFORM, puppetLifecycle);
        try {
            puppetPlugin.execute();
        } catch (Exception e) {
            logger.error("Error while executing transformation.", e);
        }
        TServiceTemplate kubeServiceTemplate = kubernetesPlugin.retrieveGeneratedServiceTemplate();
        TServiceTemplate puppetServiceTemplate = puppetPlugin.retrieveGeneratedServiceTemplate();

        TServiceTemplate mergedServiceTemplate = mergeServiceTemplate(kubeServiceTemplate, puppetServiceTemplate);
        toscaTransformer.save(mergedServiceTemplate);
    }

    private TServiceTemplate mergeServiceTemplate(TServiceTemplate aServiceTemplate, TServiceTemplate aOtherServiceTemplate) {
        TTopologyTemplate topologyTemplate = aServiceTemplate.getTopologyTemplate();
        TTopologyTemplate otherTopologyTemplate = aOtherServiceTemplate.getTopologyTemplate();

        BackendUtils.mergeTopologyTemplateAinTopologyTemplateB(otherTopologyTemplate, topologyTemplate);
        return aServiceTemplate;
    }
}

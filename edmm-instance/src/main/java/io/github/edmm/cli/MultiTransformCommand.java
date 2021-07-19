package io.github.edmm.cli;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.plugins.kubernetes.KubernetesInstancePlugin;
import io.github.edmm.plugins.puppet.PuppetInstancePlugin;
import io.github.edmm.plugins.terraform.TerraformInstancePlugin;
import io.github.edmm.util.CastUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "multitransform", descriptionHeading = "%n", description = "Starts a transformation from multiple source technologies to OpenTOSCA.", customSynopsis = "@|bold edmmi transform_puppet|@ @|yellow <path to edmmi yaml file>|@")
public class MultiTransformCommand extends TransformCommand {
    private static final String CONFIG_PUPPET_IP = "ip";
    private static final String CONFIG_TECHNOLOGY_INSTANCES = "technology-instances";
    private static final String CONFIG_KUBERNETES_KUBE_CONFIG_PATH = "kube-config-path";
    private static final String CONFIG_KUBERNETES_INPUT_DEPLOYMENT_NAME = "input-deployment-name";
    private static final String CONFIG_PUPPET_USER = "user";
    private static final String CONFIG_PUPPET_PRIVATE_KEY_PATH = "private-key-path";
    private static final String CONFIG_PUPPET_PORT = "port";
    private static final String CONFIG_PUPPET_OPERATING_SYSTEM = "operating-system";
    private static final String CONFIG_PUPPET_OPERATING_SYSTEM_VERSION = "operating-system-version";
    private static final String CONFIG_TERRAFORM_STATE_FILE_PATH = "state-file";
    private static final Logger logger = LoggerFactory.getLogger(MultiTransformCommand.class);
    private static final SourceTechnology MULTI_TRANSFORM = SourceTechnology.builder()
        .id("multitransform")
        .name("Multi Transform")
        .build();
    private static final SourceTechnology KUBERNETES = SourceTechnology.builder()
        .id("kubernetes")
        .name("Kubernetes")
        .build();
    private static final SourceTechnology TERRAFORM = SourceTechnology.builder()
        .id("terraform")
        .name("Terraform")
        .build();
    private static final SourceTechnology PUPPET = SourceTechnology.builder().id("puppet").name("Puppet").build();
    @CommandLine.Option(names = {"-p", "--port"}, defaultValue = "22")
    private Integer port;
    @CommandLine.Option(names = {"-c", "--configFile"}, required = true)
    private String configFilePath;

    @Override
    public void run() {
        Path configFile = checkConfigFileArgument();

        Map<String, Object> technologyInstances = parseConfigFile(configFile);

        Collection<InstancePlugin<KubernetesInstancePlugin>> kubernetesPlugins = parseKubernetesConfig(
            technologyInstances);

        Collection<InstancePlugin<PuppetInstancePlugin>> puppetPlugins = parsePuppetConfig(technologyInstances);

        Collection<InstancePlugin<TerraformInstancePlugin>> terraformPlugins = parseTerraformConfig(technologyInstances);

        UUID uuid = UUID.randomUUID();
        TTopologyTemplate topologyTemplate = new TTopologyTemplate();
        TServiceTemplate serviceTemplate = new TServiceTemplate.Builder("multitransform-" + uuid,
            topologyTemplate).setName("multitransform-" + uuid)
            .setTargetNamespace("http://opentosca.org/retrieved/instances")
            .addTags(new TTags.Builder().addTag("deploymentTechnology", MULTI_TRANSFORM.getName()).build())
            .build();

        for (InstancePlugin<KubernetesInstancePlugin> kubernetesPlugin : kubernetesPlugins) {
            String contextId = kubernetesPlugin.getLifecycle().getContext().getId();
            logger.info("Executing kubernetes transformation |" + contextId + "|");

            try {
                kubernetesPlugin.getLifecycle().updateGeneratedServiceTemplate(serviceTemplate);
                kubernetesPlugin.execute();
                serviceTemplate = kubernetesPlugin.retrieveGeneratedServiceTemplate();
            } catch (Exception e) {
                logger.error("Error executing kubernetes transformation |" + contextId + "|", e);
            }
        }

        for (InstancePlugin<PuppetInstancePlugin> puppetPlugin : puppetPlugins) {
            String contextId = puppetPlugin.getLifecycle().getContext().getId();
            logger.info("Executing puppet transformation |" + contextId + "|");
            try {
                puppetPlugin.getLifecycle().updateGeneratedServiceTemplate(serviceTemplate);
                puppetPlugin.execute();
                serviceTemplate = puppetPlugin.retrieveGeneratedServiceTemplate();
            } catch (Exception e) {
                logger.error("Error executing puppet transformation |" + contextId + "|", e);
            }
        }

        for (InstancePlugin<TerraformInstancePlugin> terraformPlugin : terraformPlugins) {
            String contextId = terraformPlugin.getLifecycle().getContext().getId();
            logger.info("Executing terraform transformation |" + contextId + "|");
            try {
                terraformPlugin.getLifecycle().updateGeneratedServiceTemplate(serviceTemplate);
                terraformPlugin.execute();
                serviceTemplate = terraformPlugin.retrieveGeneratedServiceTemplate();
            } catch (Exception e) {
                logger.error("Error executing terraform transformation |" + contextId + "|", e);
            }
        }

        TOSCATransformer toscaTransformer = new TOSCATransformer();
        toscaTransformer.save(serviceTemplate);
    }

    private Collection<InstancePlugin<PuppetInstancePlugin>> parsePuppetConfig(Map<String, Object> technologyInstances) {
        return Optional.ofNullable(technologyInstances.get("puppet"))
            .flatMap(CastUtil::safelyCastToStringObjectMapOptional)
            .map(Map::entrySet)
            .map(entries -> entries.stream().map(puppetInstance -> {
                Map<String, Object> puppetConfig = CastUtil.safelyCastToStringObjectMap(puppetInstance.getValue());
                String instanceId = puppetInstance.getKey();
                String user = Optional.ofNullable(puppetConfig.get(CONFIG_PUPPET_USER))
                    .map(Objects::toString)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new IllegalArgumentException("Missing config key |" + CONFIG_PUPPET_USER + "| for technology instance |" + instanceId + "|"));
                String ip = Optional.ofNullable(puppetConfig.get(CONFIG_PUPPET_IP))
                    .map(Objects::toString)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new IllegalArgumentException("Missing config key |" + CONFIG_PUPPET_IP + "| for technology instance |" + instanceId + "|"));
                String privateKeyPath = Optional.ofNullable(puppetConfig.get(CONFIG_PUPPET_PRIVATE_KEY_PATH))
                    .map(Object::toString)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new IllegalArgumentException("Missing config key |" + CONFIG_PUPPET_PRIVATE_KEY_PATH + "| for technology instance |" + instanceId + "|"));
                Integer port1 = Optional.ofNullable(puppetConfig.get(CONFIG_PUPPET_PORT))
                    .map(Objects::toString)
                    .filter(StringUtils::isNotBlank)
                    .map(Integer::valueOf)
                    .orElse(22);
                String operatingSystem = Optional.ofNullable(puppetConfig.get(CONFIG_PUPPET_OPERATING_SYSTEM))
                    .map(Object::toString)
                    .filter(StringUtils::isNotBlank)
                    .orElse(null);
                String operatingSystemVersion = Optional.ofNullable(puppetConfig.get(
                    CONFIG_PUPPET_OPERATING_SYSTEM_VERSION))
                    .map(Object::toString)
                    .filter(StringUtils::isNotBlank)
                    .orElse(null);

                InstanceTransformationContext context = new InstanceTransformationContext(instanceId,
                    PUPPET,
                    outputPath,
                    true);
                PuppetInstancePlugin instancePluginLifecycle = new PuppetInstancePlugin(context,
                    user,
                    ip,
                    privateKeyPath,
                    port1,
                    operatingSystem,
                    operatingSystemVersion);
                return new InstancePlugin<>(context.getSourceTechnology(), instancePluginLifecycle);
            }).collect(Collectors.toSet()))
            .orElse(Collections.emptySet());
    }

    private Set<InstancePlugin<KubernetesInstancePlugin>> parseKubernetesConfig(Map<String, Object> technologyInstances) {
        return Optional.ofNullable(technologyInstances.get("kubernetes"))
            .flatMap(CastUtil::safelyCastToStringObjectMapOptional)
            .map(Map::entrySet)
            .map(entries -> entries.stream().map(kubernetesInstance -> {
                Map<String, Object> kubeConfig = CastUtil.safelyCastToStringObjectMap(kubernetesInstance.getValue());
                String instanceId = kubernetesInstance.getKey();
                String kubeConfigPath = Optional.ofNullable(kubeConfig.get(CONFIG_KUBERNETES_KUBE_CONFIG_PATH))
                    .map(Object::toString)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new IllegalArgumentException("Missing config key |" + CONFIG_KUBERNETES_KUBE_CONFIG_PATH + "| for technology instance |" + instanceId + "|"));
                String inputDeploymentName = Optional.ofNullable(kubeConfig.get(CONFIG_KUBERNETES_INPUT_DEPLOYMENT_NAME))
                    .map(Object::toString)
                    .filter(StringUtils::isNotBlank)
                    .orElse(null);

                InstanceTransformationContext context = new InstanceTransformationContext(instanceId,
                    KUBERNETES,
                    outputPath,
                    true);
                KubernetesInstancePlugin kubernetesInstancePlugin = new KubernetesInstancePlugin(context,
                    kubeConfigPath,
                    inputDeploymentName);
                return new InstancePlugin<>(KUBERNETES, kubernetesInstancePlugin);
            }).collect(Collectors.toSet()))
            .orElse(Collections.emptySet());
    }

    private Set<InstancePlugin<TerraformInstancePlugin>> parseTerraformConfig(Map<String, Object> technologyInstances) {
        return Optional.ofNullable(technologyInstances.get("terraform"))
            .flatMap(CastUtil::safelyCastToStringObjectMapOptional)
            .map(Map::entrySet)
            .map(entries -> entries.stream().map(terraformInstance -> {
                Map<String, Object> terraformConfig = CastUtil.safelyCastToStringObjectMap(terraformInstance.getValue());
                String instanceId = terraformInstance.getKey();
                Path terraformStateFilePath = Optional.ofNullable(terraformConfig.get(CONFIG_TERRAFORM_STATE_FILE_PATH))
                    .map(Object::toString)
                    .filter(StringUtils::isNotBlank)
                    .map(Paths::get)
                    .orElseThrow(() -> new IllegalArgumentException("Missing config key |" + CONFIG_TERRAFORM_STATE_FILE_PATH + "| for technology instance |" + instanceId + "|"));

                InstanceTransformationContext context = new InstanceTransformationContext(instanceId,
                    TERRAFORM,
                    outputPath,
                    true);
                TerraformInstancePlugin terraformInstancePlugin = new TerraformInstancePlugin(context,
                    terraformStateFilePath);
                return new InstancePlugin<>(TERRAFORM, terraformInstancePlugin);
            }).collect(Collectors.toSet()))
            .orElse(Collections.emptySet());
    }

    private Map<String, Object> parseConfigFile(Path configFile) {
        Map<String, Object> config;
        try (InputStream configInput = Files.newInputStream(configFile)) {
            Yaml yaml = new Yaml();
            config = yaml.load(configInput);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load config file |" + configFile + "|", e);
        }

        return CastUtil.safelyCastToStringObjectMapOptional(config.get(CONFIG_TECHNOLOGY_INSTANCES))
            .orElseThrow(() -> new IllegalArgumentException("No technology instances provided in configuration"));
    }

    private Path checkConfigFileArgument() {
        if (StringUtils.isBlank(configFilePath)) {
            throw new IllegalArgumentException("Missing config file");
        }
        Path configFile;
        try {
            configFile = Paths.get(configFilePath);
            if (!Files.isRegularFile(configFile)) {
                throw new IllegalArgumentException("Config file must be a regular file");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid config file path", e);
        }
        return configFile;
    }

    private TTopologyTemplate mergeTopologyTemplate(
        TTopologyTemplate topologyTemplate, TTopologyTemplate otherTopologyTemplate) {

        if (topologyTemplate == null) {
            return otherTopologyTemplate;
        }
        if (otherTopologyTemplate == null) {
            return topologyTemplate;
        }

        BackendUtils.mergeTopologyTemplateAinTopologyTemplateB(otherTopologyTemplate, topologyTemplate);
        return topologyTemplate;
    }
}

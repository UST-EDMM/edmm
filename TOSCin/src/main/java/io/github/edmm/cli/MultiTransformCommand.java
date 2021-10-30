package io.github.edmm.cli;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.plugins.cfn.CfnInstancePlugin;
import io.github.edmm.plugins.kubernetes.KubernetesInstancePlugin;
import io.github.edmm.plugins.puppet.PuppetInstancePlugin;
import io.github.edmm.plugins.terraform.TerraformInstancePlugin;
import io.github.edmm.util.CastUtil;
import io.github.edmm.util.Constants;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "multitransform", descriptionHeading = "%n", description = "Starts a transformation from multiple source technologies to OpenTOSCA.", customSynopsis = "@|bold edmmi transform_puppet|@ @|yellow <path to edmmi yaml file>|@")
public class MultiTransformCommand extends TransformCommand {
    private static final String EMPTY_JSON_LIST = "[]";
    private static final String CONFIG_PUPPET_IP = "ip";
    private static final String CONFIG_MODEL_NAME = "model-name";
    private static final String CONFIG_TECHNOLOGY_INSTANCES = "technology-instances";
    private static final String CONFIG_KUBERNETES_KUBE_CONFIG_PATH = "kube-config-path";
    private static final String CONFIG_KUBERNETES_INPUT_DEPLOYMENT_NAME = "input-deployment-name";
    private static final String CONFIG_KUBERNETES_TARGET_NAMESPACE = "target-namespace";
    private static final String CONFIG_PUPPET_USER = "user";
    private static final String CONFIG_PUPPET_PRIVATE_KEY_PATH = "private-key-path";
    private static final String CONFIG_PUPPET_PORT = "port";
    private static final String CONFIG_PUPPET_OPERATING_SYSTEM = "operating-system";
    private static final String CONFIG_PUPPET_OPERATING_SYSTEM_VERSION = "operating-system-version";
    private static final String CONFIG_TERRAFORM_STATE_FILE_PATH = "state-file";
    private static final String CONFIG_CFN_STACK_NAME = "stack-name";
    private static final String CONFIG_CFN_PROFILE_NAME = "profile-name";
    private static final String CONFIG_CFN_REGION = "region";
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
    private static final SourceTechnology CFN = SourceTechnology.builder().id("cfn").name("CFN").build();
    private static final SourceTechnology PUPPET = SourceTechnology.builder().id("puppet").name("Puppet").build();
    @CommandLine.Option(names = {"-c", "--configFile"}, required = true)
    private String configFilePath;

    private String modelName;

    @Override
    public void run() {
        Path configFile = checkConfigFileArgument();

        Map<String, Object> technologyInstances = parseConfigFile(configFile);

        Collection<InstancePlugin<KubernetesInstancePlugin>> kubernetesPlugins = parseKubernetesConfig(
            technologyInstances);

        Collection<InstancePlugin<PuppetInstancePlugin>> puppetPlugins = parsePuppetConfig(technologyInstances);

        Collection<InstancePlugin<TerraformInstancePlugin>> terraformPlugins = parseTerraformConfig(technologyInstances);

        Collection<InstancePlugin<CfnInstancePlugin>> cfnPlugins = parseCfnConfig(technologyInstances);

        List<InstancePlugin<? extends AbstractLifecycleInstancePlugin<? extends AbstractLifecycleInstancePlugin<?>>>> plugins = new ArrayList<>();
        plugins.addAll(kubernetesPlugins);
        plugins.addAll(puppetPlugins);
        plugins.addAll(terraformPlugins);
        plugins.addAll(cfnPlugins);

        if (StringUtils.isBlank(modelName)) {
            modelName = UUID.randomUUID().toString();
        }

        if (!plugins.isEmpty()) {
            TTopologyTemplate topologyTemplate = new TTopologyTemplate();
            TServiceTemplate serviceTemplate = new TServiceTemplate.Builder("multitransform-" + modelName,
                topologyTemplate).setName("multitransform-" + modelName)
                .setTargetNamespace("http://opentosca.org/retrieved/instances")
                .addTags(new TTags.Builder().addTag("deploymentTechnology", MULTI_TRANSFORM.getName())
                    .addTag(Constants.TAG_DEPLOYMENT_TECHNOLOGIES, EMPTY_JSON_LIST)
                    .build())
                .build();

            for (InstancePlugin<? extends AbstractLifecycleInstancePlugin<? extends AbstractLifecycleInstancePlugin<?>>> curPlugin : plugins) {
                String contextId = curPlugin.getLifecycle().getContext().getId();
                String sourceTechnologyName = curPlugin.getLifecycle().getContext().getSourceTechnology().getName();
                logger.info("Executing |{}| transformation |{}|", sourceTechnologyName, contextId);
                try {
                    curPlugin.getLifecycle().updateGeneratedServiceTemplate(serviceTemplate);
                    curPlugin.execute();
                    serviceTemplate = curPlugin.getLifecycle().retrieveGeneratedServiceTemplate();
                } catch (Exception e) {
                    logger.error("Error executing |{}| transformation |{}|", sourceTechnologyName, contextId, e);
                }
            }

            TOSCATransformer toscaTransformer = new TOSCATransformer();
            toscaTransformer.save(serviceTemplate);
        }
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
                String targetNamespace = Optional.ofNullable(kubeConfig.get(CONFIG_KUBERNETES_TARGET_NAMESPACE))
                    .map(Object::toString)
                    .filter(StringUtils::isNotBlank)
                    .orElse(null);

                InstanceTransformationContext context = new InstanceTransformationContext(instanceId,
                    KUBERNETES,
                    outputPath,
                    true);
                KubernetesInstancePlugin kubernetesInstancePlugin = new KubernetesInstancePlugin(context,
                    kubeConfigPath,
                    inputDeploymentName,
                    targetNamespace);
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

    private Set<InstancePlugin<CfnInstancePlugin>> parseCfnConfig(Map<String, Object> technologyInstances) {
        return Optional.ofNullable(technologyInstances.get("cloud-formation"))
            .flatMap(CastUtil::safelyCastToStringObjectMapOptional)
            .map(Map::entrySet)
            .map(entries -> entries.stream().map(cfnInstance -> {
                Map<String, Object> cfnConfig = CastUtil.safelyCastToStringObjectMap(cfnInstance.getValue());
                String instanceId = cfnInstance.getKey();
                String region = Optional.ofNullable(cfnConfig.get(CONFIG_CFN_REGION))
                    .map(Object::toString)
                    .filter(StringUtils::isNotBlank)
                    .orElse("us-east-1");
                String stackName = Optional.ofNullable(cfnConfig.get(CONFIG_CFN_STACK_NAME))
                    .map(Object::toString)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new IllegalArgumentException("Missing config key |" + CONFIG_CFN_STACK_NAME + "| for technology instance |" + instanceId + "|"));
                String profileName = Optional.ofNullable(cfnConfig.get(CONFIG_CFN_PROFILE_NAME))
                    .map(Object::toString)
                    .filter(StringUtils::isNotBlank)
                    .orElse(null);

                InstanceTransformationContext context = new InstanceTransformationContext(instanceId,
                    CFN,
                    outputPath,
                    true);
                CfnInstancePlugin cfnInstancePlugin = new CfnInstancePlugin(context, stackName, region, profileName);
                return new InstancePlugin<>(CFN, cfnInstancePlugin);
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

        Optional.ofNullable(config.get(CONFIG_MODEL_NAME))
            .ifPresent(configModelName -> modelName = configModelName.toString());

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
}

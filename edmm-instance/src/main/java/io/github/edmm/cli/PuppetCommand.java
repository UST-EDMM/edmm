package io.github.edmm.cli;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.plugins.puppet.PuppetInstancePlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(
    name = "puppet",
    descriptionHeading = "%n",
    description = "Starts a transformation from Puppet to OpenTOSCA.",
    customSynopsis = "@|bold edmmi transform_puppet|@ @|yellow <path to edmmi yaml file>|@"
)
public class PuppetCommand extends TransformCommand {

    private static final Logger logger = LoggerFactory.getLogger(PuppetCommand.class);
    private static final SourceTechnology PUPPET = SourceTechnology.builder().id("puppet").name("Puppet").build();

    @CommandLine.Option(names = {"-u", "--user"}, required = true)
    private String user;
    @CommandLine.Option(names = {"-ip", "--masterIp"}, required = true)
    private String ip;
    @CommandLine.Option(names = {"-f", "--privateKeyFileLocation"}, required = true)
    private String privateKeyLocation;
    @CommandLine.Option(names = {"-p", "--port"}, defaultValue = "22")
    private Integer port;
    @CommandLine.Option(names = {"-os", "--operatingSystem"})
    private String operatingSystem;
    @CommandLine.Option(names = {"-osr", "--operatingSystemRelease"})
    private String operatingSystemRelease;

    @Override
    public void run() {
        InstanceTransformationContext context = new InstanceTransformationContext(PUPPET, outputPath);
        PuppetInstancePlugin pluginLifecycle = new PuppetInstancePlugin(context, user, ip, privateKeyLocation, port, operatingSystem, operatingSystemRelease);
        InstancePlugin<PuppetInstancePlugin> plugin = new InstancePlugin<>(PUPPET, pluginLifecycle);
        try {
            plugin.execute();
        } catch (Exception e) {
            logger.error("Error while executing transformation.", e);
        }
    }
}

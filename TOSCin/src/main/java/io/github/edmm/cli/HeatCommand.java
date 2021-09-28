package io.github.edmm.cli;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.plugins.heat.HeatInstancePlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(
    name = "heat",
    descriptionHeading = "%n",
    description = "Starts a transformation from OpenStack Heat to OpenTOSCA.",
    customSynopsis = "@|bold edmmi transform_puppet|@ @|yellow <path to edmmi yaml file>|@"
)
public class HeatCommand extends TransformCommand {

    private static final Logger logger = LoggerFactory.getLogger(HeatCommand.class);
    private static final SourceTechnology HEAT = SourceTechnology.builder().id("heat").name("Heat").build();

    @CommandLine.Option(names = {"-u", "--user"}, required = true)
    private String user;
    @CommandLine.Option(names = {"-p", "--password"}, required = true)
    private String password;
    @CommandLine.Option(names = {"-proj", "--projectId"}, required = true)
    private String projectId;
    @CommandLine.Option(names = {"-d", "--domainName"}, required = true)
    private String domainName;
    @CommandLine.Option(names = {"-auth", "--authenticationEndpoint"}, required = true)
    private String authenticationEndpoint;
    @CommandLine.Option(names = {"-sn", "--stackName"})
    private String stackName;
    @CommandLine.Option(names = {"-si", "--stackId"})
    private String stackId;

    @Override
    public void run() {
        InstanceTransformationContext context = new InstanceTransformationContext(HEAT, outputPath);
        HeatInstancePlugin pluginLifecycle = new HeatInstancePlugin(context, user, password, projectId, domainName,
            authenticationEndpoint, stackName, stackId);
        InstancePlugin<HeatInstancePlugin> plugin = new InstancePlugin<>(HEAT, pluginLifecycle);
        try {
            plugin.execute();
        } catch (Exception e) {
            logger.error("Error while executing transformation.", e);
        }
    }
}

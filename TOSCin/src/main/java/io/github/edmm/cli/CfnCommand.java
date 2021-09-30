package io.github.edmm.cli;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.plugins.cfn.CfnInstancePlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(
    name = "cfn",
    descriptionHeading = "%n",
    description = "Starts a transformation from AWS CloudFormation to OpenTOSCA.",
    customSynopsis = "@|bold edmmi cfn|@ @|yellow <path to edmmi yaml file>|@"
)
public class CfnCommand extends TransformCommand {

    private static final SourceTechnology CFN = SourceTechnology.builder().id("cfn").name("CFN").build();
    private static final Logger logger = LoggerFactory.getLogger(CfnCommand.class);

    @CommandLine.Option(names = {"-id", "--applicationId"}, required = true,
        description = "You need to specify the CFN stack name of the application you want to transform.")
    private String applicationId;

    @Override
    public void run() {
        InstanceTransformationContext context = new InstanceTransformationContext(applicationId, CFN, outputPath);
        CfnInstancePlugin pluginLifecycle = new CfnInstancePlugin(context);
        InstancePlugin<CfnInstancePlugin> plugin = new InstancePlugin<>(CFN, pluginLifecycle);
        try {
            plugin.execute();
        } catch (Exception e) {
            logger.error("Error while executing transformation.", e);
        }
    }
}

package io.github.edmm.cli;

import java.io.File;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.plugins.edmmi.EDMMiPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(
    name = "parse",
    descriptionHeading = "%n",
    description = "Starts a transformation from EDMMi yaml file to OpenTOSCA.",
    customSynopsis = "@|bold edmmi parse|@ @|yellow <source technology>|@ @|yellow <path to edmmi yaml file>|@"
)
public class ParseCommand extends TransformCommand {

    private final static Logger logger = LoggerFactory.getLogger(ParseCommand.class);
    private static final SourceTechnology EDMMi = SourceTechnology.builder().id("edmmi").name("EDMMi").build();

    private String inputPath;

    @CommandLine.Option(names = {"-f", "--file"}, description = "The edmmi file to transform.")
    public void setInputPath(String path) {
        if (!new File(path).exists()) {
            String message = String.format("Specified input file does not exist: %s", path);
            throw new CommandLine.ParameterException(spec.commandLine(), message);
        }
        this.inputPath = path;
    }

    @Override
    public void run() {
        InstanceTransformationContext context = new InstanceTransformationContext(EDMMi, inputPath);
        EDMMiPlugin pluginLifecycle = new EDMMiPlugin(context);
        InstancePlugin<EDMMiPlugin> plugin = new InstancePlugin<>(EDMMi, pluginLifecycle);
        try {
            plugin.execute();
        } catch (Exception e) {
            logger.error("Error while executing transformation.", e);
        }
    }
}

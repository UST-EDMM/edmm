package io.github.edmm.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.plugins.terraform.TerraformInstancePlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "terraform", descriptionHeading = "%n", description = "Starts a transformation from Terraform to OpenTOSCA.", customSynopsis = "@|bold edmmi transform_terraform|@ @|yellow <path to edmmi yaml file>|@")
public class TerraformCommand extends TransformCommand {

    private static final Logger logger = LoggerFactory.getLogger(TerraformCommand.class);
    private static final SourceTechnology TERRAFORM = SourceTechnology.builder()
        .id("terraform")
        .name("Terraform")
        .build();

    @CommandLine.Option(names = {"-f", "--stateFile"}, required = true)
    private String terraformStateFile;

    @Override
    public void run() {
        InstanceTransformationContext context = new InstanceTransformationContext(TERRAFORM, outputPath);
        Path stateFile = checkStateFilePath();
        TerraformInstancePlugin pluginLifecycle = new TerraformInstancePlugin(context, stateFile);
        InstancePlugin<TerraformInstancePlugin> plugin = new InstancePlugin<>(TERRAFORM, pluginLifecycle);
        try {
            plugin.execute();
        } catch (Exception e) {
            logger.error("Error while executing transformation.", e);
        }
    }

    private Path checkStateFilePath() {
        Path path = Paths.get(terraformStateFile);
        if (Files.notExists(path)) {
            throw new IllegalArgumentException("State file |" + path + "| does not exist");
        }
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("State file |" + path + "| must be a regular file");
        }
        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException("State file |" + path + "| must be readable");
        }
        return path;
    }
}

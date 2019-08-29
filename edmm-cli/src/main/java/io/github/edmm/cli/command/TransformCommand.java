package io.github.edmm.cli.command;

import java.io.File;

import io.github.edmm.cli.PluginService;
import io.github.edmm.cli.TransformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(
        name = "transform",
        descriptionHeading = "%n",
        description = "Starts a transformation to a target technology",
        customSynopsis = "@|bold edmm transform|@ @|yellow <target>|@ @|yellow <input>|@"
)
public class TransformCommand implements Runnable {

    @CommandLine.Parameters(arity = "1..1", index = "0", description = "The name of the transformation target")
    private String target;

    @CommandLine.Parameters(arity = "1..1", index = "1..*", description = "The input EDMM file (YAML)")
    private File input;

    private final TransformationService transformationService;
    private final PluginService pluginService;

    @Autowired
    public TransformCommand(TransformationService transformationService, PluginService pluginService) {
        this.transformationService = transformationService;
        this.pluginService = pluginService;
    }

    @Override
    public void run() {
        System.out.println(pluginService.getPlugins());
        System.out.println(transformationService.hashCode());
    }
}

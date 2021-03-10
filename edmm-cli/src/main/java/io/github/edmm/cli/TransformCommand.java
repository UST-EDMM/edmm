package io.github.edmm.cli;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import io.github.edmm.core.plugin.PluginService;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationService;
import io.github.edmm.model.DeploymentModel;

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
public class TransformCommand implements Callable<Integer> {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    private String target;
    private File input;
    private File repository;

    private TransformationService transformationService;
    private PluginService pluginService;

    @CommandLine.Parameters(arity = "1..1", index = "0", description = "The name of the transformation target")
    public void setTarget(String target) {
        List<String> availableTargets = pluginService.getTransformationPlugins().stream()
            .map(p -> p.getDeploymentTechnology().getId()).collect(Collectors.toList());
        if (!availableTargets.contains(target)) {
            String message = String.format("Specified target technology not supported. Valid values are: %s", availableTargets);
            throw new CommandLine.ParameterException(spec.commandLine(), message);
        }
        this.target = target;
    }

    @CommandLine.Parameters(arity = "1..1", index = "1..*", description = "The input EDMM file (YAML)")
    public void setInput(File input) {
        if (!input.exists() || !input.isFile()) {
            throw new CommandLine.ParameterException(spec.commandLine(), "An existing file must be specified, but was " + input.getAbsolutePath());
        }
        this.input = input;
    }

    @CommandLine.Option(names = {"-r", "--repository"}, description = "The path to the repository directory")
    public void setRepository(File repository) {
        if (!repository.exists() || !repository.isDirectory()) {
            throw new CommandLine.ParameterException(spec.commandLine(), "An existing directory must be specified, but was " + repository.getAbsolutePath());
        }
        this.repository = repository;
    }

    @Override
    public Integer call() {
        File sourceDirectory = repository;
        if (sourceDirectory == null) {
            sourceDirectory = input.getParentFile();
        }
        File targetDirectory = new File(input.getParentFile(), target);
        DeploymentModel model = DeploymentModel.of(input);
        TransformationContext context = transformationService.createContext(model, target, sourceDirectory, targetDirectory);
        transformationService.start(context);
        return 0;
    }

    @Autowired
    public void setTransformationService(TransformationService transformationService) {
        this.transformationService = transformationService;
    }

    @Autowired
    public void setPluginService(PluginService pluginService) {
        this.pluginService = pluginService;
    }
}

package io.github.edmm.cli;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import io.github.edmm.core.execution.ExecutionContext;
import io.github.edmm.core.execution.ExecutionService;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.parameters.UserInput;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

// @Component
@CommandLine.Command(
    name = "deploy",
    descriptionHeading = "%n",
    description = "Deploys a transformation result to the selected target technology",
    customSynopsis = "@|bold edmm deploy|@ @|yellow <directory>|@ @|yellow input>|@"
)
public class DeployCommand implements Callable<Integer> {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    private File directory;
    private Set<UserInput> userInputs;

    private ExecutionService executionService;

    @CommandLine.Parameters(arity = "1..1", index = "0", description = "The directory location of the EDMM transformation output")
    public void setDirectory(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            throw new CommandLine.ParameterException(spec.commandLine(), "An existing directory must be specified");
        }
        this.directory = directory;
    }

    @CommandLine.Option(names = {"-i", "--input"})
    public void setUserInputs(Map<String, String> userInputs) {
        this.userInputs = userInputs.entrySet().stream()
            .map(e -> new UserInput(e.getKey(), e.getValue()))
            .collect(Collectors.toSet());
    }

    @Override
    public Integer call() {
        ExecutionContext context = new ExecutionContext(TransformationContext.of(directory), userInputs);
        executionService.start(context);
        return 0;
    }

    @Autowired
    public void setExecutionService(ExecutionService executionService) {
        this.executionService = executionService;
    }
}

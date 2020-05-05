package io.github.edmm;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import io.github.edmm.core.plugin.InstancePluginService;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.InstanceTransformationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(
    name = "parse",
    descriptionHeading = "%n",
    description = "Starts a transformation from EDiMM yaml file to OpenTOSCA.",
    customSynopsis = "@|bold edimm parse|@ @|yellow <source technology>|@ @|yellow <path to edimm yaml file>|@"
)
public class ParseCommand implements Callable<Integer> {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    private String source;
    private String inputPath;

    private InstanceTransformationService instanceTransformationService;
    private InstancePluginService instancePluginService;

    @CommandLine.Parameters(arity = "1..1", index = "0", description = "The name of the transformation source, i.e. edimm")
    public void setSource(String source) {
        List<String> availableSources = instancePluginService.getInstancePlugins().stream()
            .map(p -> p.getSourceTechnology().getId()).collect(Collectors.toList());
        if (!availableSources.contains(source)) {
            String message = String.format("Specified source technology not supported. Valid values are: %s", availableSources);
            throw new CommandLine.ParameterException(spec.commandLine(), message);
        }
        this.source = source;
    }

    @CommandLine.Parameters(arity = "1..1", index = "1", description = "The path of the YAML input file")
    public void setInputPath(String path) {
        if (!new File(path).exists()) {
            String message = String.format("Specified input file does not exist: %s", path);
            throw new CommandLine.ParameterException(spec.commandLine(), message);
        }
        this.inputPath = path;
    }

    @Override
    public Integer call() {
        InstanceTransformationContext context = instanceTransformationService.createContext(source, inputPath);
        instanceTransformationService.startTransformation(context);
        return 42;
    }

    @Autowired
    public void setInstanceTransformationService(InstanceTransformationService instanceTransformationService) {
        this.instanceTransformationService = instanceTransformationService;
    }

    @Autowired
    public void setInstancePluginService(InstancePluginService instancePluginService) {
        this.instancePluginService = instancePluginService;
    }
}

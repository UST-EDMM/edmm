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
    name = "transform",
    descriptionHeading = "%n",
    description = "Starts a transformation from a source technology to EDiMM and saves to output path.",
    customSynopsis = "@|bold edimm transform|@ @|yellow <source technology>|@ @|yellow <output path>|@ @|yellow <application id/name>|@"
)
public class TransformCommand implements Callable<Integer> {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    private String source;
    private String outputPath;
    private String applicationId;

    private InstanceTransformationService instanceTransformationService;
    private InstancePluginService instancePluginService;

    @CommandLine.Parameters(arity = "1..1", index = "0", description = "The name of the transformation source")
    public void setSource(String source) {
        List<String> availableSources = instancePluginService.getInstancePlugins().stream()
            .map(p -> p.getSourceTechnology().getId()).collect(Collectors.toList());
        if (!availableSources.contains(source)) {
            String message = String.format("Specified source technology not supported. Valid values are: %s", availableSources);
            throw new CommandLine.ParameterException(spec.commandLine(), message);
        }
        this.source = source;
    }

    @CommandLine.Parameters(arity = "1..1", index = "1", description = "The path where output is to be saved")
    public void setOutputPath(String path) {
        if (!new File(path).isDirectory()) {
            String message = String.format("Specified output directory does not exist: %s", path);
            throw new CommandLine.ParameterException(spec.commandLine(), message);
        }
        this.outputPath = path;
    }

    @CommandLine.Parameters(arity = "1..1", index = "2", description = "The identifier of the application to be transformed and enriched")
    public void setApplicationId(String applicationId) {
        if (applicationId == null) {
            String message = String.format("Please specify an identifier for the application to be transformed.");
            throw new CommandLine.ParameterException(spec.commandLine(), message);
        }
        this.applicationId = applicationId;
    }

    @Override
    public Integer call() {
        InstanceTransformationContext context = instanceTransformationService.createContext(source, outputPath, applicationId);
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

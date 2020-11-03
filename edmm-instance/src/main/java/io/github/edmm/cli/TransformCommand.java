package io.github.edmm.cli;

import java.io.File;

import io.github.edmm.core.plugin.InstancePluginService;
import io.github.edmm.core.transformation.InstanceTransformationService;

import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine;

public abstract class TransformCommand implements Runnable {

    protected String outputPath;
//    protected String applicationId;
    protected InstanceTransformationService instanceTransformationService;

    @CommandLine.Spec
    protected CommandLine.Model.CommandSpec spec;

    private InstancePluginService instancePluginService;

    @CommandLine.Option(names = {"-o", "--outputPath"}, defaultValue = "./")
    public void setOutputPath(String path) {
        if (!new File(path).isDirectory()) {
            String message = String.format("Specified output directory does not exist: %s", path);
            throw new CommandLine.ParameterException(spec.commandLine(), message);
        }
        this.outputPath = path;
    }

//    @CommandLine.Option(names = {"-a", "--applicationId"}, description = "The identifier of the application to be transformed and enriched")
//    public void setApplicationId(String applicationId) {
//        if (applicationId == null) {
//            String message = "Please specify an identifier for the application to be transformed.";
//            throw new CommandLine.ParameterException(spec.commandLine(), message);
//        }
//        this.applicationId = applicationId;
//    }

    @Autowired
    public void setInstanceTransformationService(InstanceTransformationService instanceTransformationService) {
        this.instanceTransformationService = instanceTransformationService;
    }
}

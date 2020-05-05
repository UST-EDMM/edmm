package io.github.edmm.plugins.azure;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.BashScript;
import io.github.edmm.core.JsonHelper;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.visitor.VisitorHelper;
import io.github.edmm.plugins.ComputeSupportVisitor;
import io.github.edmm.plugins.azure.model.ResourceManagerTemplate;
import io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions.CustomScriptSettings;
import io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions.EnvVarVirtualMachineExtension;
import io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions.VirtualMachineExtension;
import io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions.VirtualMachineExtensionProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureLifecycle extends AbstractLifecycle {

    public static final String FILE_NAME = "deploy.json";

    private static final Logger logger = LoggerFactory.getLogger(AzureLifecycle.class);

    public AzureLifecycle(TransformationContext context) {
        super(context);
    }

    @Override
    public CheckModelResult checkModel() {
        ComputeSupportVisitor visitor = new ComputeSupportVisitor(context);
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        return visitor.getResult();
    }

    private void populateAzureTemplateFile(ResourceManagerTemplate resultTemplate) {
        PluginFileAccess fileAccess = context.getFileAccess();
        try {
            final String templateString = JsonHelper.writeValue(resultTemplate);
            fileAccess.append(FILE_NAME, templateString);
        } catch (IOException e) {
            logger.error("Failed to write Azure Resource Manager file: {}", e.getMessage(), e);
            throw new TransformationException(e);
        }
    }

    private void addParametersAndVariables(ResourceManagerTemplate resultTemplate) {
        resultTemplate.getResources().forEach(resource -> {
            // The following statements should discard duplicates
            resultTemplate.getParameters().putAll(resource.getRequiredParameters());
            resultTemplate.getVariables().putAll(resource.getRequiredVariables());
        });
    }

    private void copyOperationsToTargetDirectory(ResourceManagerTemplate resultTemplate) {
        List<String> toCopy = resultTemplate.getResources()
            .stream()
            .filter(resource -> resource instanceof VirtualMachineExtension && !(resource instanceof EnvVarVirtualMachineExtension)).map(resource -> {
                VirtualMachineExtension extension = (VirtualMachineExtension) resource;
                VirtualMachineExtensionProperties properties = (VirtualMachineExtensionProperties) extension.getProperties();
                CustomScriptSettings settings = properties.getSettings();
                return settings.getFileUrls();
            })
            .flatMap(List::stream)
            .collect(Collectors.toList());
        toCopy.forEach(toCopyArtifact -> {
            try {
                context.getFileAccess().copy(toCopyArtifact, toCopyArtifact);
            } catch (IOException e) {
                logger.warn("Failed to copy file '{}'", toCopyArtifact);
            }
        });
    }

    private void createEnvironmentVariableScripts(ResourceManagerTemplate resultTemplate) {
        resultTemplate.getResources()
            .stream()
            .filter(resource -> resource instanceof EnvVarVirtualMachineExtension)
            .forEach(resource -> {
                EnvVarVirtualMachineExtension extension = (EnvVarVirtualMachineExtension) resource;
                extension.getScriptPath().ifPresent(path -> {
                    BashScript envScript = new BashScript(context.getFileAccess(), path);
                    extension.getEnvironmentVariables().forEach((key, value) -> {
                        envScript.append("export " + key + "=" + value);
                    });
                });
            });
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Azure Resource Manager...");
        AzureVisitor visitor = new AzureVisitor(context.getTopologyGraph());
        // Visit compute components first
        VisitorHelper.visit(context.getModel().getComponents(), visitor, component -> component instanceof Compute);
        // ... then all others
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        VisitorHelper.visit(context.getModel().getRelations(), visitor);
        // ... then populate variables and parameters required by the added azure resources
        ResourceManagerTemplate resultTemplate = visitor.getResultTemplate();
        this.addParametersAndVariables(resultTemplate);
        // ... then write result to disk!
        this.populateAzureTemplateFile(resultTemplate);
        // ... then for each "stack" create a bash script that adds the stack's properties as environment variables!
        this.createEnvironmentVariableScripts(resultTemplate);
        // ... then copy artifact files to target directory
        this.copyOperationsToTargetDirectory(resultTemplate);
        logger.info("Transformation to Azure Resource Manager successful");
    }
}

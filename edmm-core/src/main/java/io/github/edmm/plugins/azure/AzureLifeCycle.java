package io.github.edmm.plugins.azure;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.JsonHelper;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.visitor.VisitorHelper;
import io.github.edmm.plugins.azure.model.ResourceManagerTemplate;
import io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions.CustomScriptSettings;
import io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions.VirtualMachineExtension;
import io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions.VirtualMachineExtensionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureLifeCycle extends AbstractLifecycle {
    public static final String FILE_NAME = "deploy.json";
    private static final Logger logger = LoggerFactory.getLogger(AzureLifeCycle.class);
    private final TransformationContext context;

    public AzureLifeCycle(TransformationContext context) {
        this.context = context;
    }

    private void populateAzureTemplateFile(ResourceManagerTemplate resultTemplate) {
        PluginFileAccess fileAccess = context.getFileAccess();
        try {
            final String templateString = JsonHelper.serializeObj(resultTemplate);
            logger.debug(templateString);
            fileAccess.append(FILE_NAME, templateString);
        } catch (IOException e) {
            logger.error("Failed to write Azure Resource Manager file: {}", e.getMessage(), e);
        }
    }

    private void addParametersAndVariables(ResourceManagerTemplate resultTemplate) {
        resultTemplate.getResources().forEach(resource -> {
            // the following statements should discard duplicates
            resultTemplate.getParameters().putAll(resource.getRequiredParameters());
            resultTemplate.getVariables().putAll(resource.getRequiredVariables());
        });
    }

    private void copyOperationsToTargetDirectory(ResourceManagerTemplate resultTemplate) {
        List<String> toCopy = resultTemplate.getResources()
                .stream()
                .filter(resource -> resource instanceof VirtualMachineExtension).map(resource -> {
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
        // ... then copy artifact files to target directory
        this.copyOperationsToTargetDirectory(resultTemplate);
        logger.info("Transformation to Terraform successful");
    }
}

package io.github.edmm.plugins.cfn;

import java.io.File;

import com.scaleset.cfbuilder.ec2.metadata.CFNCommand;
import com.scaleset.cfbuilder.ec2.metadata.CFNFile;
import io.github.edmm.model.Artifact;
import io.github.edmm.model.Operation;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;

import static io.github.edmm.plugins.cfn.CloudFormationModule.CONFIG_CONFIGURE;
import static io.github.edmm.plugins.cfn.CloudFormationModule.CONFIG_CREATE;
import static io.github.edmm.plugins.cfn.CloudFormationModule.CONFIG_SETS;
import static io.github.edmm.plugins.cfn.CloudFormationModule.CONFIG_START;
import static io.github.edmm.plugins.cfn.CloudFormationModule.MODE_777;
import static io.github.edmm.plugins.cfn.CloudFormationModule.OWNER_GROUP_ROOT;

public class OperationHandler {

    private final CloudFormationModule module;

    public OperationHandler(CloudFormationModule module) {
        this.module = module;
    }

    public void handleCreate(RootComponent component, Compute compute) {
        module.getOperations(compute).getOrAddConfig(CONFIG_SETS, CONFIG_CREATE);
        if (component.getStandardLifecycle().getCreate().isPresent()) {
            Operation create = component.getStandardLifecycle().getCreate().get();
            handleOperation(create, compute, CONFIG_CREATE);
        }
    }

    public void handleConfigure(RootComponent component, Compute compute) {
        module.getOperations(compute).getOrAddConfig(CONFIG_SETS, CONFIG_CONFIGURE);
        if (component.getStandardLifecycle().getConfigure().isPresent()) {
            Operation configure = component.getStandardLifecycle().getConfigure().get();
            handleOperation(configure, compute, CONFIG_CONFIGURE);
        }
    }

    public void handleStart(RootComponent component, Compute compute) {
        module.getOperations(compute).getOrAddConfig(CONFIG_SETS, CONFIG_START);
        if (component.getStandardLifecycle().getStart().isPresent()) {
            Operation start = component.getStandardLifecycle().getStart().get();
            handleOperation(start, compute, CONFIG_START);
        }
    }

    private void handleOperation(Operation operation, Compute compute, String config) {
        if (operation.getArtifacts().size() > 0) {
            Artifact artifact = operation.getArtifacts().get(0);
            String file = artifact.getValue();
            String name = artifact.getNormalizedValue();
            if (file.startsWith("./")) {
                file = file.substring(2);
            }
            CFNCommand cfnCommand = prepareCommand(file, name);
            CFNFile cfnFile = prepareFile(file);
            module.getOperations(compute)
                .getOrAddConfig(CONFIG_SETS, config)
                .putFile(cfnFile)
                .putCommand(cfnCommand);
        }
    }

    private CFNCommand prepareCommand(String file, String name) {
        String parent = new File(file).getParent();
        if (parent == null) {
            parent = "";
        }
        return new CFNCommand(name, "/opt/" + file).setCwd("/opt/" + parent);
    }

    private CFNFile prepareFile(String file) {
        String source = String.format("http://%s.s3.amazonaws.com/%s", module.getBucketName(), file);
        return new CFNFile("/opt/" + file)
            .setSource(source)
            .setMode(MODE_777) // TODO
            .setOwner(OWNER_GROUP_ROOT) // TODO
            .setGroup(OWNER_GROUP_ROOT);
    }
}

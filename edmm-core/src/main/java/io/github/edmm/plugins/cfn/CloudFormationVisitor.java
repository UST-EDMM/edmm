package io.github.edmm.plugins.cfn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.TopologyGraphHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.Operation;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.MysqlDatabase;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.Tomcat;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;

import com.google.common.collect.Lists;
import com.scaleset.cfbuilder.core.Fn;
import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import com.scaleset.cfbuilder.ec2.metadata.CFNFile;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.plugins.cfn.CloudFormationModule.CONFIG_INIT;
import static io.github.edmm.plugins.cfn.CloudFormationModule.CONFIG_SETS;
import static io.github.edmm.plugins.cfn.CloudFormationModule.MODE_777;
import static io.github.edmm.plugins.cfn.CloudFormationModule.OWNER_GROUP_ROOT;
import static io.github.edmm.plugins.cfn.CloudFormationModule.SECURITY_GROUP;

public class CloudFormationVisitor implements ComponentVisitor, RelationVisitor {

    private static final Logger logger = LoggerFactory.getLogger(CloudFormationVisitor.class);

    private final TransformationContext context;
    private final CloudFormationModule module;
    private final Graph<RootComponent, RootRelation> graph;
    private final OperationHandler operationHandler;
    private final EnvHandler envHandler;

    public CloudFormationVisitor(TransformationContext context, CloudFormationModule module) {
        this.context = context;
        this.module = module;
        this.graph = context.getTopologyGraph();
        this.operationHandler = new OperationHandler(module);
        this.envHandler = new EnvHandler(module, context.getFileAccess());
    }

    public void complete() {
        // Set environment variable for connected components
        for (Pair<RootComponent, RootComponent> connection : module.getConnectionPairs()) {
            RootComponent sourceComponent = connection.getLeft();
            RootComponent targetComponent = connection.getRight();
            if (module.containsFn(targetComponent.getNormalizedName())) {
                TopologyGraphHelper.resolveHostingComputeComponent(graph, sourceComponent)
                    .ifPresent(compute -> {
                        String name = targetComponent.getNormalizedName().toUpperCase() + "_HOSTNAME";
                        module.addEnvVar(compute, name, module.getFn(targetComponent.getNormalizedName()));
                    });
            }
        }
        envHandler.handleEnvVars();
    }

    @Override
    public void visit(Compute component) {
        if (!module.containsComputeResource(component)) {
            String name = component.getNormalizedName();
            logger.debug("Compute '{}' will be transformed to EC2", name);
            // Default security group the EC2 Instance
            SecurityGroup securityGroup = module
                .resource(SecurityGroup.class, name + SECURITY_GROUP);
            // Add EC2 instance
            Instance instance = module.resource(Instance.class, name)
                .securityGroupIds(securityGroup)
                .imageId("ami-0bbc25e23a7640b9b")
                .instanceType("t2.micro");
            module.addComputeResource(component);
            // Enable SSH port
            if (module.isKeyPair()) {
                instance.keyName(module.getKeyNameVar());
                module.addPortMapping(component, 22);
            }
            // Add "Init" operation to be executed first
            module.getOperations(component).getOrAddConfig(CONFIG_SETS, CONFIG_INIT);
        }
        component.setTransformed(true);
    }

    @Override
    public void visit(MysqlDatabase component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
            .orElseThrow(TransformationException::new);
        // Add operations and properties
        visit(component, compute);
        component.setTransformed(true);
    }

    @Override
    public void visit(MysqlDbms component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
            .orElseThrow(TransformationException::new);
        // Open port
        component.getPort().ifPresent(port -> module.addPortMapping(compute, port));
        // Add operations and properties
        visit(component, compute);
        component.setTransformed(true);
    }

    @Override
    public void visit(WebApplication component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
            .orElseThrow(TransformationException::new);
        // Add operations and properties
        visit(component, compute);
        component.setTransformed(true);
    }

    @Override
    public void visit(Tomcat component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
            .orElseThrow(TransformationException::new);
        // Open port
        component.getPort().ifPresent(port -> module.addPortMapping(compute, port));
        // Add operations and properties
        visit(component, compute);
        component.setTransformed(true);
    }

    private void visit(RootComponent component, Compute compute) {
        operationHandler.handleCreate(component, compute);
        operationHandler.handleConfigure(component, compute);
        operationHandler.handleStart(component, compute);
        prepareProperties(component, compute);
        copyOperations(component);
        copyArtifacts(component, compute);
        // Fn pointer to public ip address
        Fn publicIp = Fn.fnGetAtt(compute.getNormalizedName(), "PublicIp");
        module.addFn(component.getNormalizedName(), publicIp);
    }

    @Override
    public void visit(ConnectsTo relation) {
        RootComponent sourceComponent = graph.getEdgeSource(relation);
        RootComponent targetComponent = graph.getEdgeTarget(relation);
        Optional<Compute> optionalSourceCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, sourceComponent);
        Optional<Compute> optionalTargetCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, targetComponent);
        if (optionalSourceCompute.isPresent() && optionalTargetCompute.isPresent()) {
            module.addConnectionPair(new ImmutablePair<>(sourceComponent, targetComponent));
        }
    }

    private void prepareProperties(RootComponent component, Compute compute) {
        Map<String, String> envVars = new HashMap<>();
        preparePropertiesFromUnderlyingStack(envVars, component);
        preparePropertiesFromConnectedStack(envVars, component);
        envVars.forEach((name, value) -> module.addEnvVar(compute, name, value));
    }

    private void preparePropertiesFromUnderlyingStack(Map<String, String> envVars, RootComponent component) {
        List<RootComponent> stack = Lists.newArrayList(component);
        TopologyGraphHelper.resolveChildComponents(graph, stack, component);
        doPrepareProperties(envVars, stack);
    }

    private void preparePropertiesFromConnectedStack(Map<String, String> envVars, RootComponent component) {
        for (Pair<RootComponent, RootComponent> connection : module.getConnectionPairs()) {
            RootComponent sourceComponent = connection.getLeft();
            if (!sourceComponent.equals(component)) {
                continue;
            }
            RootComponent targetComponent = connection.getRight();
            TopologyGraphHelper.resolveHostingComputeComponent(graph, targetComponent)
                .ifPresent(targetCompute -> {
                    List<RootComponent> targetStack = Lists.newArrayList(targetComponent);
                    TopologyGraphHelper.resolveChildComponents(graph, targetStack, targetComponent);
                    doPrepareProperties(envVars, targetStack);
                });
        }
    }

    private void doPrepareProperties(Map<String, String> envVars, List<RootComponent> stack) {
        String[] blacklist = {"key_name", "public_key"};
        for (RootComponent component : stack) {
            Map<String, Property> properties = component.getProperties();
            properties.values().stream()
                .filter(p -> !Arrays.asList(blacklist).contains(p.getName()))
                .forEach(p -> {
                    String name = (component.getNormalizedName() + "_" + p.getNormalizedName()).toUpperCase();
                    envVars.put(name, p.getValue());
                });
        }
    }

    private void copyOperations(RootComponent component) {
        List<Operation> operations = new ArrayList<>();
        component.getStandardLifecycle().getCreate().ifPresent(operations::add);
        component.getStandardLifecycle().getConfigure().ifPresent(operations::add);
        component.getStandardLifecycle().getStart().ifPresent(operations::add);
        operations.forEach(operation -> {
            if (!operation.getArtifacts().isEmpty()) {
                String file = operation.getArtifacts().get(0).getValue();
                PluginFileAccess fileAccess = context.getFileAccess();
                try {
                    fileAccess.copy(file, file);
                } catch (IOException e) {
                    logger.warn("Failed to copy file '{}'", file);
                }
            }
        });
    }

    private void copyArtifacts(RootComponent component, Compute compute) {
        component.getArtifacts().forEach(artifact -> {
            String file = artifact.getValue();
            String filename = file;
            if (filename.startsWith("./")) {
                filename = filename.substring(2);
            }
            String source = String.format("http://%s.s3.amazonaws.com/%s", module.getBucketName(), filename);
            CFNFile cfnFile = new CFNFile("/opt/" + filename)
                .setSource(source)
                .setMode(MODE_777)
                .setOwner(OWNER_GROUP_ROOT)
                .setGroup(OWNER_GROUP_ROOT);
            module.getOperations(compute)
                .getOrAddConfig(CONFIG_SETS, CONFIG_INIT)
                .putFile(cfnFile);
            PluginFileAccess fileAccess = context.getFileAccess();
            try {
                fileAccess.copy(file, file);
            } catch (IOException e) {
                logger.warn("Failed to copy file '{}'", file);
            }
        });
    }
}

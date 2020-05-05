package io.github.edmm.plugins.heat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.TopologyGraphHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.Artifact;
import io.github.edmm.model.Operation;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Database;
import io.github.edmm.model.component.Dbms;
import io.github.edmm.model.component.MysqlDatabase;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.component.Tomcat;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;
import io.github.edmm.plugins.heat.model.Parameter;
import io.github.edmm.plugins.heat.model.PropertyAssignment;
import io.github.edmm.plugins.heat.model.PropertyGetAttr;
import io.github.edmm.plugins.heat.model.PropertyGetFile;
import io.github.edmm.plugins.heat.model.PropertyGetParam;
import io.github.edmm.plugins.heat.model.PropertyGetResource;
import io.github.edmm.plugins.heat.model.PropertyObject;
import io.github.edmm.plugins.heat.model.PropertyValue;
import io.github.edmm.plugins.heat.model.Resource;
import io.github.edmm.plugins.heat.model.Template;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeatVisitor implements ComponentVisitor, RelationVisitor {

    private static final String HEAT_COMPUTE_TYPE = "OS::Nova::Server";
    private static final String HEAT_PORT_TYPE = "OS::Neutron::Port";
    private static final String HEAT_FLOATING_IP_TYPE = "OS::Neutron::FloatingIP";
    private static final String HEAT_FLOATING_IP_ASSOC_TYPE = "OS::Neutron::FloatingIPAssociation";
    private static final String HEAT_SOFTWARE_DEPLOYMENT_TYPE = "OS::Heat::SoftwareDeployment";
    private static final String HEAT_SOFTWARE_CONFIG_TYPE = "OS::Heat::SoftwareConfig";

    private static final String HEAT_KEY_NAME = "key_name";
    private static final String HEAT_FLAVOR = "flavor";
    private static final String HEAT_IMAGE = "image";
    private static final String HEAT_NETWORKS = "networks";
    private static final String HEAT_NETWORK = "network";
    private static final String HEAT_SECURITY_GROUPS = "security_groups";
    private static final String HEAT_SECURITY_GROUP = "security_group";
    private static final String HEAT_PORT = "port";
    private static final String HEAT_FLOATING_NETWORK = "floating_network";
    private static final String HEAT_FLOATINGIP_ID = "floatingip_id";
    private static final String HEAT_PORT_ID = "port_id";
    private static final String HEAT_USER_DATA_FORMAT = "user_data_format";
    private static final String HEAT_SOFTWARE_CONFIG = "SOFTWARE_CONFIG";
    private static final String HEAT_CONFIG = "config";
    private static final String HEAT_SERVER = "server";
    private static final String HEAT_INPUT_VALUES = "input_values";
    private static final String HEAT_GROUP = "group";
    private static final String HEAT_SCRIPT = "script";
    private static final String HEAT_INPUTS = "inputs";
    private static final String HEAT_NAME = "name";

    private static final Logger logger = LoggerFactory.getLogger(HeatVisitor.class);

    private final TransformationContext context;
    private final Graph<RootComponent, RootRelation> graph;
    private final Template template;

    private final Map<Compute, Resource> computeResources = new HashMap<>();
    private final List<Pair<RootComponent, RootComponent>> connectionPairs = new ArrayList<>();

    public HeatVisitor(TransformationContext context) {
        this.context = context;
        this.graph = context.getTopologyGraph();
        this.template = new Template();
        this.template.setName(context.getModel().getName());
    }

    public void populateHeatTemplate() {
        PluginFileAccess fileAccess = context.getFileAccess();
        try {
            fileAccess.append(template.getName(), template.toYaml());
        } catch (IOException e) {
            logger.error("Failed to write Terraform file", e);
            throw new TransformationException(e);
        }
    }

    private void handleSoftwareDeployment(RootComponent component) {

        // Mapping base on
        // https://docs.openstack.org/heat/pike/template_guide/software_deployment.html

        Optional<Compute> optionalCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component);
        if (optionalCompute.isPresent()) {
            Compute compute = optionalCompute.get();
            Resource instance = computeResources.get(compute);

            List<Operation> operations = collectOperations(component);
            for (int i = 0; i < operations.size(); i++) {
                Operation operation = operations.get(i);

                // We only deal with one artifact
                Artifact artifact = operation.getArtifacts().get(0);
                // Set properties on instance
                instance.addPropertyAssignment(HEAT_USER_DATA_FORMAT, new PropertyValue(HEAT_SOFTWARE_CONFIG));
                // Create software deployment resource
                Resource deployment = Resource.builder()
                    .name(getDeploymentName(component, operation))
                    .type(HEAT_SOFTWARE_DEPLOYMENT_TYPE)
                    .build();
                // Create respective config resource
                Resource config = Resource.builder()
                    .name(getConfigName(component, operation))
                    .type(HEAT_SOFTWARE_CONFIG_TYPE)
                    .build();
                deployment.addDependsOn(instance.getName(), config.getName());
                // Set order of operations
                if (i > 0) {
                    Operation previousOperation = operations.get(i - 1);
                    deployment.addDependsOn(getDeploymentName(component, previousOperation));
                }
                // Set deployment properties
                deployment.addPropertyAssignment(HEAT_CONFIG, new PropertyGetResource(config.getName()));
                deployment.addPropertyAssignment(HEAT_SERVER, new PropertyGetResource(instance.getName()));
                // Collect properties from underlying stack
                List<RootComponent> stack = Lists.newArrayList(component);
                TopologyGraphHelper.resolveChildComponents(context.getTopologyGraph(), stack, component);
                Map<String, Object> inputValues = new HashMap<>();
                prepareProperties(inputValues, stack);
                // Collect properties from connected stacks
                for (Pair<RootComponent, RootComponent> connection : connectionPairs) {
                    RootComponent sourceComponent = connection.getLeft();
                    if (!sourceComponent.equals(component)) {
                        continue;
                    }
                    RootComponent targetComponent = connection.getRight();
                    TopologyGraphHelper.resolveHostingComputeComponent(graph, targetComponent)
                        .ifPresent(targetCompute -> {
                            List<Object> attrs = Lists.newArrayList(targetCompute.getNormalizedName(), "networks", "private", "0");
                            List<RootComponent> targetStack = Lists.newArrayList(targetComponent);
                            TopologyGraphHelper.resolveChildComponents(context.getTopologyGraph(), targetStack, targetComponent);
                            prepareProperties(inputValues, targetStack);
                            inputValues.put(targetComponent.getNormalizedName() + "_hostname", new PropertyGetAttr(attrs));
                        });
                }
                deployment.addPropertyAssignment(HEAT_INPUT_VALUES, new PropertyObject(inputValues));
                // Set artifact and copy file
                config.addPropertyAssignment(HEAT_GROUP, new PropertyValue(HEAT_SCRIPT));
                List<ImmutablePair<String, String>> inputs = new ArrayList<>();
                inputValues.forEach((name, value) -> inputs.add(new ImmutablePair<>(HEAT_NAME, name)));
                config.addPropertyAssignment(HEAT_INPUTS, new PropertyObject(inputs));
                config.addPropertyAssignment(HEAT_CONFIG, new PropertyGetFile(artifact.getValue()));
                PluginFileAccess fileAccess = context.getFileAccess();
                try {
                    fileAccess.copy(artifact.getValue(), artifact.getValue());
                } catch (IOException e) {
                    logger.warn("Failed to copy file '{}'", artifact.getValue());
                }
                template.addResource(deployment, config);
            }
        }
        component.setTransformed(true);
    }

    private String getDeploymentName(RootComponent component, Operation operation) {
        return component.getNormalizedName() + "_" + operation.getNormalizedName();
    }

    private String getConfigName(RootComponent component, Operation operation) {
        return getDeploymentName(component, operation) + "_config";
    }

    @Override
    public void visit(Compute compute) {

        // Mapping based on
        // https://docs.openstack.org/heat/pike/template_guide/basic_resources.html

        Resource port = Resource.builder()
            .name(compute.getNormalizedName() + "_port")
            .type(HEAT_PORT_TYPE)
            .build();
        port.addPropertyAssignment(HEAT_NETWORK, new PropertyGetParam(HEAT_NETWORK));
        List<Object> securityGroups = Lists.newArrayList(new PropertyGetParam(HEAT_SECURITY_GROUP));
        port.addPropertyAssignment(HEAT_SECURITY_GROUPS, new PropertyObject(securityGroups));
        template.addResource(port);

        Resource instance = Resource.builder()
            .name(compute.getNormalizedName())
            .type(HEAT_COMPUTE_TYPE)
            .build();
        instance.addDependsOn(port.getName());
        template.addParameter(Parameter.builder().name(HEAT_KEY_NAME).type("string").build());
        template.addParameter(Parameter.builder().name(HEAT_IMAGE).type("string").build());
        template.addParameter(Parameter.builder().name(HEAT_FLAVOR).type("string").build());
        template.addParameter(Parameter.builder()
            .name(HEAT_NETWORK).type("string").defaultValue("default").build());
        template.addParameter(Parameter.builder()
            .name(HEAT_SECURITY_GROUP).type("string").defaultValue("default").build());
        instance.addPropertyAssignment(HEAT_KEY_NAME, new PropertyGetParam(HEAT_KEY_NAME));
        instance.addPropertyAssignment(HEAT_IMAGE, new PropertyGetParam(HEAT_IMAGE));
        instance.addPropertyAssignment(HEAT_FLAVOR, new PropertyGetParam(HEAT_FLAVOR));
        List<ImmutablePair<String, PropertyAssignment>> networks = Lists.newArrayList(
            new ImmutablePair<>(HEAT_PORT, new PropertyGetResource(port.getName()))
        );
        instance.addPropertyAssignment(HEAT_NETWORKS, new PropertyObject(networks));
        template.addResource(instance);

        Resource floatingIp = Resource.builder()
            .name(compute.getNormalizedName() + "_floating_ip")
            .type(HEAT_FLOATING_IP_TYPE)
            .build();
        floatingIp.addDependsOn(port.getName());
        floatingIp.addPropertyAssignment(HEAT_FLOATING_NETWORK, new PropertyGetParam(HEAT_NETWORK));
        floatingIp.addPropertyAssignment(HEAT_PORT_ID, new PropertyGetResource(port.getName()));
        template.addResource(floatingIp);

        Resource floatingIpAssoc = Resource.builder()
            .name(compute.getNormalizedName() + "_floating_ip_association")
            .type(HEAT_FLOATING_IP_ASSOC_TYPE)
            .build();
        floatingIpAssoc.addDependsOn(floatingIp.getName(), port.getName());
        floatingIpAssoc.addPropertyAssignment(HEAT_FLOATINGIP_ID, new PropertyGetResource(floatingIp.getName()));
        floatingIpAssoc.addPropertyAssignment(HEAT_PORT_ID, new PropertyGetResource(port.getName()));
        template.addResource(floatingIpAssoc);

        computeResources.put(compute, instance);
        compute.setTransformed(true);
    }

    @Override
    public void visit(Database component) {
        handleSoftwareDeployment(component);
    }

    @Override
    public void visit(Dbms component) {
        handleSoftwareDeployment(component);
    }

    @Override
    public void visit(MysqlDatabase component) {
        handleSoftwareDeployment(component);
    }

    @Override
    public void visit(MysqlDbms component) {
        handleSoftwareDeployment(component);
    }

    @Override
    public void visit(SoftwareComponent component) {
        handleSoftwareDeployment(component);
    }

    @Override
    public void visit(Tomcat component) {
        handleSoftwareDeployment(component);
    }

    @Override
    public void visit(WebApplication component) {
        handleSoftwareDeployment(component);
    }

    @Override
    public void visit(WebServer component) {
        handleSoftwareDeployment(component);
    }

    @Override
    public void visit(ConnectsTo relation) {
        RootComponent sourceComponent = graph.getEdgeSource(relation);
        RootComponent targetComponent = graph.getEdgeTarget(relation);
        Optional<Compute> optionalSourceCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, sourceComponent);
        Optional<Compute> optionalTargetCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, targetComponent);
        if (optionalSourceCompute.isPresent() && optionalTargetCompute.isPresent()) {
            connectionPairs.add(new ImmutablePair<>(sourceComponent, targetComponent));
        }
    }

    private void prepareProperties(Map<String, Object> inputs, List<RootComponent> stack) {
        String[] blacklist = {"key_name", "public_key"};
        for (RootComponent component : stack) {
            Map<String, Property> properties = component.getProperties();
            properties.values().stream()
                .filter(p -> !Arrays.asList(blacklist).contains(p.getName()))
                .forEach(p -> inputs.put(component.getNormalizedName() + "_" + p.getNormalizedName(), p.getValue()));
        }
    }

    private List<Operation> collectOperations(RootComponent component) {
        List<Operation> operations = new ArrayList<>();
        component.getStandardLifecycle().getCreate().ifPresent(operations::add);
        component.getStandardLifecycle().getConfigure().ifPresent(operations::add);
        component.getStandardLifecycle().getStart().ifPresent(operations::add);
        return operations
            .stream()
            .filter(Operation::hasArtifacts)
            .collect(Collectors.toList());
    }
}

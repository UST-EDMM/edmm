package io.github.edmm.plugins.heat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
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
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;
import io.github.edmm.plugins.heat.model.Parameter;
import io.github.edmm.plugins.heat.model.PropertyAssignment;
import io.github.edmm.plugins.heat.model.PropertyGetParam;
import io.github.edmm.plugins.heat.model.PropertyGetResource;
import io.github.edmm.plugins.heat.model.PropertyObject;
import io.github.edmm.plugins.heat.model.Resource;
import io.github.edmm.plugins.heat.model.Template;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeatVisitor implements ComponentVisitor, RelationVisitor {

    private static final String HEAT_COMPUTE_TYPE = "OS::Nova::Server";
    private static final String HEAT_PORT_TYPE = "OS::Neutron::Port";
    private static final String HEAT_FLOATING_IP_TYPE = "OS::Neutron::FloatingIP";
    private static final String HEAT_FLOATING_IP_ASSOC_TYPE = "OS::Neutron::FloatingIPAssociation";

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

    private static final Logger logger = LoggerFactory.getLogger(HeatVisitor.class);

    private final TransformationContext context;
    private final Graph<RootComponent, RootRelation> graph;
    private final Template template;

    private Map<Compute, Resource> computeResources = new HashMap<>();

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
        component.setTransformed(true);
    }

    @Override
    public void visit(Compute component) {

        // Mapping based on
        // https://docs.openstack.org/heat/pike/template_guide/basic_resources.html

        Resource port = Resource.builder()
                .name(component.getNormalizedName() + "_port")
                .type(HEAT_PORT_TYPE)
                .build();
        port.addPropertyAssignment(HEAT_NETWORK, new PropertyGetParam(HEAT_NETWORK));
        List<Object> securityGroups = Lists.newArrayList(new PropertyGetParam(HEAT_SECURITY_GROUP));
        port.addPropertyAssignment(HEAT_SECURITY_GROUPS, new PropertyObject(securityGroups));
        template.addResource(port);

        Resource compute = Resource.builder()
                .name(component.getNormalizedName())
                .type(HEAT_COMPUTE_TYPE)
                .build();
        compute.addDependsOn(port.getName());
        template.addParameter(Parameter.builder().name(HEAT_KEY_NAME).type("string").build());
        template.addParameter(Parameter.builder().name(HEAT_IMAGE).type("string").build());
        template.addParameter(Parameter.builder().name(HEAT_FLAVOR).type("string").build());
        template.addParameter(Parameter.builder().name(HEAT_NETWORK).type("string").build());
        template.addParameter(Parameter.builder().name(HEAT_SECURITY_GROUP).type("string").build());
        compute.addPropertyAssignment(HEAT_KEY_NAME, new PropertyGetParam(HEAT_KEY_NAME));
        compute.addPropertyAssignment(HEAT_IMAGE, new PropertyGetParam(HEAT_IMAGE));
        compute.addPropertyAssignment(HEAT_FLAVOR, new PropertyGetParam(HEAT_FLAVOR));
        Map<String, PropertyAssignment> networks = new HashMap<>();
        networks.put(HEAT_PORT, new PropertyGetResource(port.getName()));
        compute.addPropertyAssignment(HEAT_NETWORKS, new PropertyObject(networks));
        template.addResource(compute);

        Resource floatingIp = Resource.builder()
                .name(component.getNormalizedName() + "_floating_ip")
                .type(HEAT_FLOATING_IP_TYPE)
                .build();
        floatingIp.addDependsOn(port.getName());
        floatingIp.addPropertyAssignment(HEAT_FLOATING_NETWORK, new PropertyGetParam(HEAT_NETWORK));
        floatingIp.addPropertyAssignment(HEAT_PORT_ID, new PropertyGetResource(port.getName()));
        template.addResource(floatingIp);

        Resource floatingIpAssoc = Resource.builder()
                .name(component.getNormalizedName() + "_floating_ip_association")
                .type(HEAT_FLOATING_IP_ASSOC_TYPE)
                .build();
        floatingIpAssoc.addDependsOn(floatingIp.getName(), port.getName());
        floatingIpAssoc.addPropertyAssignment(HEAT_FLOATINGIP_ID, new PropertyGetResource(floatingIp.getName()));
        floatingIpAssoc.addPropertyAssignment(HEAT_PORT_ID, new PropertyGetResource(port.getName()));
        template.addResource(floatingIpAssoc);

        computeResources.put(component, compute);
        component.setTransformed(true);
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
}

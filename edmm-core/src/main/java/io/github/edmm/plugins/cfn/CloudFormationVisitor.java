package io.github.edmm.plugins.cfn;

import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import io.github.edmm.core.plugin.TopologyGraphHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Dbms;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.Tomcat;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.plugins.cfn.CloudFormationModule.SECURITY_GROUP;

public class CloudFormationVisitor implements ComponentVisitor, RelationVisitor {

    private static final Logger logger = LoggerFactory.getLogger(CloudFormationVisitor.class);

    private static final String IP_OPEN = "0.0.0.0/0";
    private static final String PROTOCOL_TCP = "tcp";

    private final TransformationContext context;
    private final Graph<RootComponent, RootRelation> graph;
    private final CloudFormationModule module;

    public CloudFormationVisitor(TransformationContext context, CloudFormationModule module) {
        this.context = context;
        this.graph = context.getTopologyGraph();
        this.module = module;
    }

    @Override
    public void visit(Compute component) {
        if (!module.contains(component)) {
            logger.debug("Compute '{}' will be transformed to EC2", component.getNormalizedName());
            String name = component.getNormalizedName();
            // Default security group the EC2 Instance
            SecurityGroup securityGroup = module
                    .resource(SecurityGroup.class, name + SECURITY_GROUP);
            // Add EC2 instance
            module.resource(Instance.class, name)
                    .securityGroupIds(securityGroup)
                    .imageId("ami-0bbc25e23a7640b9b")
                    .instanceType("t2.micro");
            // Open SSH port if there is a key pair
            if (module.isKeyPair()) {
                Instance instance = (Instance) module.getResource(name);
                instance.keyName(module.getKeyNameVar());
                securityGroup.ingress(ingress -> ingress.cidrIp(IP_OPEN), PROTOCOL_TCP, 22);
            }
        }
    }

    @Override
    public void visit(Dbms component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
                .orElseThrow(TransformationException::new);
        component.getPort().ifPresent(port -> addPort(port, compute));
    }

    @Override
    public void visit(MysqlDbms component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
                .orElseThrow(TransformationException::new);
        component.getPort().ifPresent(port -> addPort(port, compute));
    }

    @Override
    public void visit(WebServer component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
                .orElseThrow(TransformationException::new);
        component.getPort().ifPresent(port -> addPort(port, compute));
    }

    @Override
    public void visit(Tomcat component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
                .orElseThrow(TransformationException::new);
        component.getPort().ifPresent(port -> addPort(port, compute));
    }

    private void addPort(Integer port, Compute compute) {
        String securityGroupName = compute.getNormalizedName() + SECURITY_GROUP;
        SecurityGroup securityGroup = (SecurityGroup) module.getResource(securityGroupName);
        securityGroup.ingress(ingress -> ingress.cidrIp(IP_OPEN), PROTOCOL_TCP, port);
    }
}

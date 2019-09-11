package io.github.edmm.plugins.cfn;

import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import io.github.edmm.core.plugin.TopologyGraphHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.Tomcat;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.plugins.cfn.CloudFormationModule.SECURITY_GROUP;

public class CloudFormationVisitor implements ComponentVisitor, RelationVisitor {

    private static final Logger logger = LoggerFactory.getLogger(CloudFormationVisitor.class);

    private final Graph<RootComponent, RootRelation> graph;
    private final CloudFormationModule module;

    public CloudFormationVisitor(TransformationContext context, CloudFormationModule module) {
        this.graph = context.getTopologyGraph();
        this.module = module;
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
        }
        component.setTransformed(true);
    }

    @Override
    public void visit(MysqlDbms component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
                .orElseThrow(TransformationException::new);
        component.getPort().ifPresent(port -> module.addPortMapping(compute, port));
    }

    @Override
    public void visit(Tomcat component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
                .orElseThrow(TransformationException::new);
        component.getPort().ifPresent(port -> module.addPortMapping(compute, port));
    }
}

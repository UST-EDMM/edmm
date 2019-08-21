package io.github.edmm.plugins.terraform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.visitor.ComponentVisitor;

public class ComputeVisitor implements ComponentVisitor {

    private final TransformationContext context;
    private final Configuration cfg;

    private final Map<RootComponent, List<String>> templateContent = new HashMap<>();

    public ComputeVisitor(TransformationContext context, Configuration cfg) {
        this.context = context;
        this.cfg = cfg;
    }

    @Override
    public void visit(Compute component) {

        Map<String, Object> data = new HashMap<>();
        data.put("aws_security_group_name", component.getNormalizedName() + "_security_group");
        data.put("ingress_template", "");
        data.put("aws_ec2_name", component.getNormalizedName());
        data.put("aws_ec2_ami", "ami-0bbc25e23a7640b9b");
        data.put("aws_ec2_instance_type", "t2.micro");
        data.put("aws_ec2_provisioner", ""); // TODO

        templateContent.put(component, new ArrayList<>());

        Template template;

        // Add defaults
        template = TemplateHelper.getTemplate(cfg, "aws_ec2_default.tf");
        templateContent.get(component).add(TemplateHelper.toString(template, data));

        // Add security group
        template = TemplateHelper.getTemplate(cfg, "aws_security_group.tf");
        templateContent.get(component).add(TemplateHelper.toString(template, data));

        template = TemplateHelper.getTemplate(cfg, "aws_ec2.tf");
        templateContent.get(component).add(TemplateHelper.toString(template, data));
    }

    public Map<RootComponent, List<String>> getTemplateContent() {
        return templateContent;
    }
}

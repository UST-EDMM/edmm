package io.github.edmm.plugins.terraform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import freemarker.template.Template;
import io.github.edmm.core.plugin.GraphHelper;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.Operation;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.Tomcat;
import io.github.edmm.utils.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.model.component.WebServer.PORT;
import static io.github.edmm.plugins.terraform.TerraformLifecycle.FILE_NAME;

public class TerraformAwsVisitor extends TerraformVisitor {

    private static final Logger logger = LoggerFactory.getLogger(TerraformAwsVisitor.class);

    private final Map<Compute, Map<String, Object>> computeTemplateData = new HashMap<>();
    private final Map<RootComponent, String> securityGroupIngressTemplateData = new HashMap<>();
    private final Map<RootComponent, List<String>> provisionerTemplateData = new HashMap<>();

    public TerraformAwsVisitor(TransformationContext context) {
        super(context);
    }

    @Override
    public void populateTerraformFile() {
        PluginFileAccess fileAccess = context.getFileAccess();

        try {
            Template baseTemplate = cfg.getTemplate("aws_base.tpl");
            fileAccess.append(FILE_NAME, TemplateHelper.toString(baseTemplate, null));

            // If there are compute nodes, add some defaults
            if (!computeTemplateData.isEmpty()) {
                fileAccess.append(FILE_NAME, TemplateHelper.toString(cfg, "aws_ec2_default.tpl", null));
            }

            for (Map.Entry<Compute, Map<String, Object>> computeEntry : computeTemplateData.entrySet()) {
                Compute compute = computeEntry.getKey();
                Map<String, Object> data = computeEntry.getValue();
                // Prepare ingress for respective security group
                StringBuilder securityGroupIngress = new StringBuilder(Consts.EMPTY);
                for (Map.Entry<RootComponent, String> componentEntry : securityGroupIngressTemplateData.entrySet()) {
                    RootComponent component = GraphHelper.getComponent(graph, componentEntry.getKey()).orElseThrow(IllegalStateException::new);
                    Optional<Compute> optionalCompute = GraphHelper.resolveHostingComputeComponent(graph, component);
                    if (optionalCompute.isPresent()) {
                        Compute hostingCompute = optionalCompute.get();
                        if (compute.equals(hostingCompute)) {
                            securityGroupIngress.append(componentEntry.getValue());
                            securityGroupIngress.append(Consts.NL);
                        }
                    }
                }
                data.put("ingress", securityGroupIngress.toString());

                // Prepare script values
                StringBuilder scripts = new StringBuilder(Consts.EMPTY);
                for (Map.Entry<RootComponent, List<String>> componentEntry : provisionerTemplateData.entrySet()) {
                    RootComponent component = GraphHelper.getComponent(graph, componentEntry.getKey()).orElseThrow(IllegalStateException::new);
                    Optional<Compute> optionalCompute = GraphHelper.resolveHostingComputeComponent(graph, component);
                    if (optionalCompute.isPresent()) {
                        Compute hostingCompute = optionalCompute.get();
                        if (compute.equals(hostingCompute)) {
                            componentEntry.getValue().stream()
                                    .filter(Objects::nonNull)
                                    .forEach(value -> scripts
                                            .append(Consts.DOUBLE_QUOTE)
                                            .append(value)
                                            .append(Consts.DOUBLE_QUOTE)
                                            .append(",")
                                            .append(Consts.NL));
                        }
                    }
                }
                data.put("scripts", scripts.toString());

                // Add provisioner statement if there are scripts available
                data.put("provisioner", Consts.NL);
                if (!scripts.toString().isEmpty()) {
                    String provisionerData = TemplateHelper.toString(cfg, "provisioner_remote_exec.tpl", data);
                    data.put("provisioner", provisionerData);
                }

                fileAccess.append(FILE_NAME, TemplateHelper.toString(cfg, "aws_security_group.tpl", data));
                fileAccess.append(FILE_NAME, TemplateHelper.toString(cfg, "aws_ec2.tpl", data));
            }
        } catch (IOException e) {
            logger.error("Failed to write Terraform file: {}", e.getMessage(), e);
        }
    }

    @Override
    public void visit(Compute component) {
        Map<String, Object> data = new HashMap<>();
        data.put("aws_ec2_name", component.getNormalizedName());
        data.put("aws_security_group_name", component.getNormalizedName() + "_security_group");
        // TODO: Try to resolve image
        data.put("aws_ec2_ami", "ami-0bbc25e23a7640b9b");
        // TODO: Try to resolve instance type
        data.put("aws_ec2_instance_type", "t2.micro");
        computeTemplateData.put(component, data);
        component.setTransformed(true);
    }

    @Override
    public void visit(Tomcat component) {
        collectIngressData(component);
        collectProvisionerScripts(component);
    }

    @Override
    public void visit(MysqlDbms component) {
        collectIngressData(component);
        collectProvisionerScripts(component);
    }

    private void collectIngressData(RootComponent component) {
        component.getProperty(PORT).ifPresent(port -> {
            Map<String, Object> data = new HashMap<>();
            data.put("from_port", port.toString());
            data.put("to_port", port.toString());
            String ingress = TemplateHelper.toString(cfg, "aws_security_group_ingress.tpl", data);
            securityGroupIngressTemplateData.put(component, ingress);
        });
    }

    private void collectProvisionerScripts(RootComponent component) {
        provisionerTemplateData.put(component, new ArrayList<>());
        Consumer<Operation> listArtifactsConsumer = op -> op.getArtifacts()
                .forEach(artifact -> provisionerTemplateData.get(component).add(artifact.getValue()));
        component.getStandardLifecycle().getCreate().ifPresent(listArtifactsConsumer);
        component.getStandardLifecycle().getConfigure().ifPresent(listArtifactsConsumer);
        component.getStandardLifecycle().getStart().ifPresent(listArtifactsConsumer);
    }
}

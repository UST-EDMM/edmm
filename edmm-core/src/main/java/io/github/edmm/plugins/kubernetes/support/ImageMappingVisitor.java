package io.github.edmm.plugins.kubernetes.support;

import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.Tomcat;
import io.github.edmm.model.visitor.ComponentVisitor;

public class ImageMappingVisitor implements ComponentVisitor {

    private String baseImage = "library/ubuntu:bionic";
    private boolean hasComputeScripts = false;

    public String getBaseImage() {
        return baseImage;
    }

    private boolean hasCreateScript(RootComponent component) {
        final boolean[] hasScript = {false};
        component.getStandardLifecycle().getCreate()
            .ifPresent(op -> hasScript[0] = op.getArtifacts().size() > 0);
        return hasScript[0];
    }

    private boolean hasConfigureScript(RootComponent component) {
        final boolean[] hasScript = {false};
        component.getStandardLifecycle().getConfigure()
            .ifPresent(op -> hasScript[0] = op.getArtifacts().size() > 0);
        return hasScript[0];
    }

    @Override
    public void visit(Compute component) {
        hasComputeScripts = hasCreateScript(component) || hasConfigureScript(component);
    }

    @Override
    public void visit(MysqlDbms component) {
        if (!hasComputeScripts && !hasCreateScript(component)) {
            baseImage = "library/mysql:8";
        }
    }

    @Override
    public void visit(Tomcat component) {
        if (!hasComputeScripts && !hasCreateScript(component)) {
            baseImage = "library/tomcat:8.5";
        }
    }
}

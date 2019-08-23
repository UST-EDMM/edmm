package io.github.edmm.model.visitor;

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

public interface ComponentVisitor {

    default void visit(Compute component) {
        // noop
    }

    default void visit(Database component) {
        // noop
    }

    default void visit(Dbms component) {
        // noop
    }

    default void visit(MysqlDatabase component) {
        // noop
    }

    default void visit(MysqlDbms component) {
        // noop
    }

    default void visit(RootComponent component) {
        // noop
    }

    default void visit(SoftwareComponent component) {
        // noop
    }

    default void visit(Tomcat component) {
        // noop
    }

    default void visit(WebApplication component) {
        // noop
    }

    default void visit(WebServer component) {
        // noop
    }
}

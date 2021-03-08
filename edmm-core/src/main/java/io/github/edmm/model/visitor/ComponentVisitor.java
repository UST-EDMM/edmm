package io.github.edmm.model.visitor;

import io.github.edmm.model.component.Auth0;
import io.github.edmm.model.component.AwsAurora;
import io.github.edmm.model.component.AwsBeanstalk;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Database;
import io.github.edmm.model.component.Dbaas;
import io.github.edmm.model.component.Dbms;
import io.github.edmm.model.component.Go;
import io.github.edmm.model.component.Mom;
import io.github.edmm.model.component.MongoDb;
import io.github.edmm.model.component.MongoDbSchema;
import io.github.edmm.model.component.MysqlDatabase;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.Platform;
import io.github.edmm.model.component.RabbitMq;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.Saas;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.component.Tomcat;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.component.WebServer;

public interface ComponentVisitor {

    default void visit(RootComponent component) {
        // noop
    }

    default void visit(Auth0 component) {
        visit((RootComponent) component);
    }

    default void visit(AwsAurora component) {
        visit((RootComponent) component);
    }

    default void visit(AwsBeanstalk component) {
        visit((RootComponent) component);
    }

    default void visit(Compute component) {
        visit((RootComponent) component);
    }

    default void visit(Database component) {
        visit((RootComponent) component);
    }

    default void visit(Dbaas component) {
        visit((RootComponent) component);
    }

    default void visit(Dbms component) {
        visit((RootComponent) component);
    }

    default void visit(Go component) {
        visit((RootComponent) component);
    }

    default void visit(MysqlDatabase component) {
        visit((RootComponent) component);
    }

    default void visit(MysqlDbms component) {
        visit((RootComponent) component);
    }

    default void visit(Paas component) {
        visit((RootComponent) component);
    }

    default void visit(Platform component) {
        visit((RootComponent) component);
    }

    default void visit(Saas component) {
        visit((RootComponent) component);
    }

    default void visit(SoftwareComponent component) {
        visit((RootComponent) component);
    }

    default void visit(Tomcat component) {
        visit((RootComponent) component);
    }

    default void visit(WebApplication component) {
        visit((RootComponent) component);
    }

    default void visit(WebServer component) {
        visit((RootComponent) component);
    }

    default void visit(Mom component) {
        visit((RootComponent) component);
    }

    default void visit(RabbitMq component) {
        visit((RootComponent) component);
    }

    default void visit(MongoDb component) {
        visit((RootComponent) component);
    }

    default void visit(MongoDbSchema component) {
        visit((RootComponent) component);
    }
}

package com.scaleset.cfbuilder.iam;

import java.util.ArrayList;
import java.util.List;

/**
 * Constructs a {@code Statement}  to build IAM policies in the {@link PolicyDocument}.
 *
 * @see <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_statement.html">Documentation
 * Reference</a>
 */
public class Statement {
    private String sid;
    private List<String> action;
    private List<String> notAction;
    private String effect;
    private Principal principal;
    private Principal notPrincipal;
    private List<String> resource;
    private List<String> notResource;
    private Object condition;

    public Statement() {
        this.action = new ArrayList<>();
        this.notAction = new ArrayList<>();
        this.resource = new ArrayList<>();
        this.notResource = new ArrayList<>();
    }

    public String getSid() {
        return sid;
    }

    public Statement sid(String sid) {
        this.sid = sid;
        return this;
    }

    public List<String> getAction() {
        return action;
    }

    public Statement action(List<String> action) {
        this.action = action;
        return this;
    }

    public Statement addAction(String action) {
        this.action.add(action);
        return this;
    }

    public List<String> getNotAction() {
        return notAction;
    }

    public Statement notAction(List<String> notAction) {
        this.notAction = notAction;
        return this;
    }

    public Statement addNotAction(String action) {
        this.notAction.add(action);
        return this;
    }

    public String getEffect() {
        return effect;
    }

    public Statement effect(String effect) {
        this.effect = effect;
        return this;
    }

    public Object getPrincipal() {
        return principal;
    }

    public Statement principal(Principal principal) {
        this.principal = principal;
        return this;
    }

    public Object getNotPrincipal() {
        return notPrincipal;
    }

    public Statement notPrincipal(Principal notPrincipal) {
        this.notPrincipal = notPrincipal;
        return this;
    }

    public List<String> getResource() {
        return resource;
    }

    public Statement resource(List<String> resource) {
        this.resource = resource;
        return this;
    }

    public Statement addResource(String resource) {
        this.resource.add(resource);
        return this;
    }

    public List<String> getNotResource() {
        return notResource;
    }

    public Statement notResource(List<String> notResource) {
        this.notResource = notResource;
        return this;
    }

    public Statement addNotResource(String resource) {
        this.notResource.add(resource);
        return this;
    }

    public Object getCondition() {
        return condition;
    }

    public Statement conditon(Object condition) {
        this.condition = condition;
        return this;
    }
}

package com.scaleset.cfbuilder.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Module {

    private String id;
    protected Template template;
    private Map<String, Object> options = new HashMap<>();

    public Module() {
    }

    public Module(Template template, String id, Map<String, Object> options) {
        template(template);
        id(id);
        options(options);
    }

    public void build() {
    }

    public String ns(String id) {
        return this.id + id;
    }

    public Ref ref(String refId) {
        return template.ref(ns(refId));
    }

    public Resource getResource(String id) {
        return template.getResource(ns(id));
    }

    public Fn fnGetAtt(String resource, String attribute) {
        return template.fnGetAtt(ns(resource), attribute);
    }

    public Parameter strParam(String id) {
        return template.strParam(ns(id));
    }

    public Parameter numParam(String id) {
        return template.numParam(ns(id));
    }

    protected Optional<Object> option(String name) {
        Object value = options.get(name);
        return (value == null) ? Optional.empty() : Optional.of(value);
    }

    public Output output(String id, Object value, String description) {
        return template.output(ns(id), value, description);
    }

    public <T extends Resource> T resource(Class<T> type, String id) {
        return template.resource(type, ns(id));
    }

    public Module id(String id) {
        this.id = id;
        return this;
    }

    public Module options(Map<String, Object> options) {
        if (options != null) {
            this.options.putAll(options);
        }
        return this;
    }

    public Module template(Template template) {
        this.template = template;
        return this;
    }
}

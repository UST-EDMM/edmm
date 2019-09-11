package com.scaleset.cfbuilder.core;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.cloudformation.Authentication;
import com.scaleset.cfbuilder.ec2.UserData;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;
import com.scaleset.cfbuilder.ec2.metadata.Config;
import com.scaleset.cfbuilder.ec2.metadata.ConfigSets;

public class ResourceInvocationHandler<T extends Resource> implements InvocationHandler {

    private final static JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

    @JsonIgnore
    private String id;

    private Class<T> resourceClass;

    private T proxy;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Properties")
    private ObjectNode properties = nodeFactory.objectNode();

    @JsonProperty("Metadata")
    private ObjectNode metadata = nodeFactory.objectNode();

    public ResourceInvocationHandler(Class<T> resourceClass, String id) {
        if (resourceClass.isAnnotationPresent(Type.class)) {
            Type type = resourceClass.getAnnotation(Type.class);
            this.type = type.value();
        } else {
            throw new IllegalArgumentException("Type annotation required");
        }
        this.id = id;
        this.resourceClass = resourceClass;
    }

    public ResourceInvocationHandler(Class<T> resourceClass, String type, String id, ObjectNode properties) {
        this.resourceClass = resourceClass;
        this.type = type;
        this.id = id;
        this.properties = properties;
    }

    public ResourceInvocationHandler(Class<T> resourceClass, String type, String id, ObjectNode properties, ObjectNode metadata) {
        this.resourceClass = resourceClass;
        this.type = type;
        this.id = id;
        this.properties = properties;
        this.metadata = metadata;
    }

    protected Object doDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {

        final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }

        Object result = null;
        if (method.isDefault()) {
            final Class<?> declaringClass = method.getDeclaringClass();
            result = constructor.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE)
                    .unreflectSpecial(method, declaringClass)
                    .bindTo(proxy)
                    .invokeWithArguments(args);
        }

        // proxy impl of not defaults methods
        return result;
    }

    protected Object doSetter(Object proxy, Method method, Object[] args) {
        Object result = null;
        if (args[0] instanceof CFNInit) { //is metadata
            CFNInit cfnInit = (CFNInit) args[0];
            JsonNode valueNode = toNode(cfnInit);
            if (!valueNode.isNull()) {
                ObjectNode cfnInitNode = this.metadata.putObject("AWS::CloudFormation::Init");
                ConfigSets configSets = cfnInit.getConfigSets();
                ObjectNode configSetsNode = cfnInitNode.putObject(configSets.getId());
                configSets.getSets().forEach((name, list) -> configSetsNode.set(name, toNode(list)));
                Map<String, Config> configs = cfnInit.getConfigs();
                configs.forEach((name, config) -> cfnInitNode.set(name, toNode(config)));
            }
        } else if (args[0] instanceof Authentication) {
            Authentication authentication = (Authentication) args[0];
            JsonNode valueNode = toNode(authentication);
            if (!valueNode.isNull()) {
                ObjectNode authenticationNode = this.metadata.putObject("AWS::CloudFormation::Authentication");
                authenticationNode.set(authentication.getName(), valueNode);
            }
        } else if (args[0] instanceof UserData) {
            UserData userData = (UserData) args[0];
            setProperty("UserData", userData);
        } else { //is property
            String propertyName = getPropertyName(method);

            // We know args.length is 1 from isSetter check method
            Object value = args[0];

            if (isArrayProperty(method, args)) {
                setArrayProperty(propertyName, (Object[]) value);
            } else {
                setProperty(propertyName, value);
            }
        }
        if (method.getReturnType().equals(resourceClass)) {
            result = proxy;
        }
        return result;
    }

    /**
     * Get the setProperty name of the variable from the getter/setter method name
     */
    private String getPropertyName(Method method) {
        String result = method.getName();
        if (result.startsWith("set") || result.startsWith("get")) {
            char[] chars = result.substring(3).toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            result = String.valueOf(chars);
        }
        result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
        return result;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        Class<?> declaringClass = method.getDeclaringClass();

        if (isGetter(method, args)) {
            String name = getPropertyName(method);
            if ("ref".equals(name)) {
                result = ref();
            } else if ("Id".equals(name)) {
                result = id;
            } else if ("Type".equals(name)) {
                result = type;
            } else if ("Properties".equals(name)) {
                return properties;
            } else if ("Metadata".equals(name)) {
                return metadata;
            }
        } else if (declaringClass.equals(Object.class)) {
            result = method.invoke(this, args);
        } else if (isRef(method, args)) {
            result = ref();
        } else if (method.isDefault()) {
            result = doDefaultMethod(proxy, method, args);
        } else if (isSetter(method, args)) {
            result = doSetter(proxy, method, args);
        } else {
            throw new UnsupportedOperationException();
        }
        return result;
    }

    protected boolean isArrayProperty(Method method, Object[] args) {
        boolean result = false;
        if (args.length == 1 && method.getParameters()[0].isVarArgs()) {
            result = true;
        }
        return result;
    }

    private boolean isGetNode(Method method, Object[] args) {
        return "getNode".equals(method.getName());
    }

    private boolean isGetter(Method method, Object[] args) {
        return method.getName().startsWith("get") && (args == null || args.length == 0);
    }

    private boolean isRef(Method method, Object[] args) {
        return "ref".equals(method.getName());
    }

    /**
     * Check whether or not method is a setter with one argument and uses optional default value if null
     */
    private boolean isSetter(Method method, Object[] args) {
        return args != null && args.length == 1;
    }

    private boolean isTag(Method method, Object[] args) {
        return "tag".equals(method.getName()) && args.length == 2;
    }

    public T proxy() {
        if (proxy == null) {
            proxy = (T) Proxy.newProxyInstance(resourceClass.getClassLoader(), new Class[] {resourceClass}, this);
        }
        return proxy;
    }

    private Ref ref() {
        return new Ref(id);
    }

    protected void setArrayProperty(String name, Object[] values) {
        ArrayNode node = properties.withArray(name);

        // Resource, Ref, Property, Function, Parameter, Primitive
        // Primitives: Float, Double,

        for (Object obj : values) {
            JsonNode valueNode = toNode(obj);
            if (!valueNode.isNull()) {
                node.add(valueNode);
            }
        }
    }

    public void setProperty(String name, Object obj) {
        JsonNode valueNode = toNode(obj);
        if (!valueNode.isNull()) {
            properties.set(name, valueNode);
        }
    }

    protected JsonNode toNode(Object obj) {
        JsonNode result;
        if (obj instanceof Ref) {
            result = ((Ref) obj).toNode();
        } else if (obj instanceof Referenceable) {
            result = ((Referenceable) obj).ref().toNode();
        } else {
            result = nodeFactory.pojoNode(obj);
        }
        return result;
    }

    protected Object toPropertyValue(Object value) {
        if (value instanceof Resource) {
            return ((Resource) value).ref();
        } else if (value instanceof Parameter) {
            return ((Parameter) value).ref();
        } else {
            return value;
        }
    }
}

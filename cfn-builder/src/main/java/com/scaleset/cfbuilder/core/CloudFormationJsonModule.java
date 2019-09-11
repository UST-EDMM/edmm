package com.scaleset.cfbuilder.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scaleset.cfbuilder.annotations.Type;
import org.reflections.Reflections;

public class CloudFormationJsonModule extends SimpleModule {

    private Map<String, Class<? extends Resource>> types = new HashMap<>();

    public CloudFormationJsonModule() {
        //  addSerializer(Resource.class, new ResourceSerializer());
        addDeserializer(Resource.class, new ResourceDeserializer());
    }

    public CloudFormationJsonModule scanTypes() {
        Reflections reflections = new Reflections("com.scaleset.cfbuilder");
        Set<Class<? extends Resource>> subTypes = reflections.getSubTypesOf(Resource.class);
        for (Class<? extends Resource> resourceClass : subTypes) {
            if (resourceClass.isAnnotationPresent(Type.class)) {
                Type typeAnn = resourceClass.getAnnotation(Type.class);
                String type = typeAnn.value();
                types.put(type, resourceClass);
            }
        }
        return this;
    }

    class ResourceDeserializer extends JsonDeserializer<Resource> {

        @Override
        public Resource deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException, JsonProcessingException {
            Resource result = null;

            ObjectCodec oc = jsonParser.getCodec();
            JsonNode node = oc.readTree(jsonParser);
            String id = node.asText();
            String type = node.get("Type").asText();
            ObjectNode properties = (ObjectNode) node.get("Properties");
            ObjectNode metadata = (ObjectNode) node.get("Metadata");
            Class<? extends Resource> resourceClass = types.get(type);
            if (resourceClass != null) {
                if (metadata != null) {
                    ResourceInvocationHandler<? extends Resource> handler =
                            new ResourceInvocationHandler<>(resourceClass, type, id, properties, metadata);
                    result = handler.proxy();
                } else {
                    ResourceInvocationHandler<? extends Resource> handler =
                            new ResourceInvocationHandler<>(resourceClass, type, id, properties);
                    result = handler.proxy();
                }
            }
            return result;
        }
    }

    static class ResourceSerializer extends JsonSerializer<Resource> {

        @Override
        public void serialize(Resource resource, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                throws IOException, JsonProcessingException {
            jsonGenerator.writeObject(resource);
        }
    }
}

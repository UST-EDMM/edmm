package io.github.edmm.core.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class JsonHelper {

    private static ObjectMapper mapper;

    public static String toJson(Object obj) throws JsonProcessingException {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
}

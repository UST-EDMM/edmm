package io.github.edmm.plugins.puppet.util;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonHelper {

    public static <T> T parseJsonStringToObjectType(String jsonString, Class<T> type) {
        Type genericType = TypeToken.get(type).getType();
        Gson gson = new Gson();
        return gson.fromJson(jsonString, genericType);
    }

    public static <T> List<T> parseJsonStringToParameterizedList(String jsonString, Class<T> type) {
        Type genericType = TypeToken.getParameterized(List.class, type).getType();
        Gson gson = new Gson();
        return gson.fromJson(jsonString, genericType);
    }

    public static Map<String, String> parseJsonStringToStringStringMap(String jsonString) {
        Type genericType = new TypeToken<Map<String, String>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(jsonString, genericType);
    }
}

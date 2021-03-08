package io.github.edmm.utils;

import java.util.Optional;

public abstract class Env {

    public static Optional<String> get(String name) {
        return Optional.ofNullable(System.getenv(name));
    }

    public static String get(String name, String defaultValue) {
        return get(name).orElse(defaultValue);
    }
}

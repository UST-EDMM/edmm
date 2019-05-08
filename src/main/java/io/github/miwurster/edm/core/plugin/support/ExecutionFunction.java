package io.github.miwurster.edm.core.plugin.support;

@FunctionalInterface
public interface ExecutionFunction<T> {

    void apply(T t) throws Exception;
}

package io.github.ust.edmm.core.plugin.support;

@FunctionalInterface
public interface ExecutionFunction<T> {

    void apply(T t) throws Exception;
}

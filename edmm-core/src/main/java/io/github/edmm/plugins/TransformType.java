package io.github.edmm.plugins;

import io.github.edmm.model.component.RootComponent;

public interface TransformType<T> {

    /**
     * Checks, whether the plugin can transform the given component to a technology specific type.
     *
     * @param component the component to transform.
     * @return whether the plugin can transform the given component.
     */
    boolean canHandle(RootComponent component);

    /**
     * Performs the transformation of the component to the technology specific type.
     *
     * @param component the component to transform
     * @return the required output for the transformation
     */
    T performTransformation(RootComponent component);
}

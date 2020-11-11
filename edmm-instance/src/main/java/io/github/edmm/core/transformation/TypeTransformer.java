package io.github.edmm.core.transformation;

import javax.xml.namespace.QName;

public interface TypeTransformer {
    /**
     * Checks, whether the plugin can transform the given component to a technology specific type.
     *
     * @param component the component to transform.
     * @return whether the plugin can transform the given component.
     */
    boolean canHandle(String component, String version);

    /**
     * Performs the transformation of the component to the technology specific type.
     *
     * @param component the component to transform
     * @return the QName of the identified TOSCA type.
     */
    QName performTransformation(String component, String version);
}

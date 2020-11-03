package io.github.edmm.core.transformation;

public class InstanceTransformationException extends RuntimeException {

    public InstanceTransformationException() {
    }

    public InstanceTransformationException(String message) {
        super(message);
    }

    public InstanceTransformationException(String message, Throwable cause) {
        super(message, cause);
    }
}

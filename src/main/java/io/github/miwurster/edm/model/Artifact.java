package io.github.miwurster.edm.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Artifact {

    private final String name;

    private final String uri;

    public Artifact(@NonNull String name, @NonNull String uri) {
        this.name = name;
        this.uri = uri;
    }
}

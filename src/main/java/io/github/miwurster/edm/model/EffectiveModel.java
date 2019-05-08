package io.github.miwurster.edm.model;

import java.io.File;
import java.io.FileInputStream;

import io.github.miwurster.edm.core.parser.EntityGraph;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public final class EffectiveModel {

    private final String name;
    private final EntityGraph graph;

    public EffectiveModel(String name, EntityGraph graph) {
        this.name = name;
        this.graph = graph;
    }

    @SneakyThrows
    public static EffectiveModel of(File file) {
        if (!file.isFile() || !file.canRead()) {
            throw new IllegalStateException(String.format("File '%s' does not exist - failed to construct internal graph", file));
        }
        EntityGraph graph = new EntityGraph(new FileInputStream(file));
        return new EffectiveModel(file.getName(), graph);
    }
}

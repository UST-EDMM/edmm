package io.github.ust.edmm.model;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import io.github.ust.edmm.core.parser.EntityGraph;
import io.github.ust.edmm.model.component.RootComponent;
import io.github.ust.edmm.model.support.TypeWrapper;
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

    private Map<String, RootComponent> components;

    public EffectiveModel(String name, EntityGraph graph) {
        this.name = name;
        this.graph = graph;

        components = TypeWrapper.wrapComponents(graph);
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

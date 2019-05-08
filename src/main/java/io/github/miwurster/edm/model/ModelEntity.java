package io.github.miwurster.edm.model;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.github.miwurster.edm.core.parser.MappingEntity;
import io.github.miwurster.edm.utils.Types;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public abstract class ModelEntity {

    private static Attribute<String> DESCRIPTION = new Attribute<>("description", String.class);
    private static Attribute<Metadata> METADATA = new Attribute<>("metadata", Types.generify(Map.class));

    private final MappingEntity entity;
    private final String name;

    public ModelEntity(MappingEntity entity) {
        this.entity = entity;
        this.name = entity.getName();
    }

    protected <T> void set(Attribute<T> key, T value) {
        entity.setValue(key, value);
    }

    protected <T> void setDefault(Attribute<T> key, T value) {
        if (entity.getValue(key) == null) {
            set(key, value);
        }
    }

    protected <T> T get(Attribute<T> key) {
        return entity.getValue(key);
    }

    protected <T> Collection<T> getCollection(Attribute<T> key) {
        return entity.getCollection(key);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(get(DESCRIPTION));
    }

    public Metadata getMetadata() {
        Metadata metadata = get(METADATA);
        if (Objects.isNull(metadata)) {
            return new Metadata();
        }
        return metadata;
    }
}

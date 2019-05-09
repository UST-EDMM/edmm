package io.github.ust.edmm.model.support;

import java.util.Objects;
import java.util.Optional;

import io.github.ust.edmm.core.parser.MappingEntity;
import io.github.ust.edmm.model.Metadata;
import io.github.ust.edmm.model.support.Attribute;
import io.github.ust.edmm.model.support.BaseElement;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class DescribableElement extends BaseElement {

    public static Attribute<String> DESCRIPTION = new Attribute<>("description", String.class);
    public static Attribute<Metadata> METADATA = new Attribute<>("metadata", Metadata.class);

    public DescribableElement(MappingEntity entity) {
        super(entity);
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

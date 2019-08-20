package io.github.edmm.model.support;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.model.Artifact;
import io.github.edmm.model.Metadata;
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

    protected void populateArtifacts(List<Artifact> result, Entity entity) {
        Set<Entity> children = entity.getChildren();
        for (Entity child : children) {
            ScalarEntity artifactEntity = (ScalarEntity) child;
            Artifact artifact = new Artifact(artifactEntity, getEntity());
            result.add(artifact);
        }
    }
}

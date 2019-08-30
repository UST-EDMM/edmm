package io.github.edmm.model.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.model.Artifact;
import io.github.edmm.model.Metadata;
import lombok.ToString;

@ToString
public abstract class DescribableElement extends BaseElement {

    public static final Attribute<String> DESCRIPTION = new Attribute<>("description", String.class);
    public static final Attribute<Metadata> METADATA = new Attribute<>("metadata", Metadata.class);
    // todo why is "artifacts" at this level of abstraction (all properties now have artifacts!)
    public static final Attribute<Artifact> ARTIFACTS = new Attribute<>("artifacts", Artifact.class);

    protected final List<MappingEntity> entityChain = new ArrayList<>();

    public DescribableElement(MappingEntity entity) {
        super(entity);
        this.entityChain.add(entity);
    }

    protected <T> T resolveByEntityChain(Attribute<T> key) {
        for (MappingEntity operationDefinition : entityChain) {
            T value = operationDefinition.getValue(key);
            if (!Objects.isNull(value)) {
                return value;
            }
        }
        return null;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(resolveByEntityChain(DESCRIPTION));
    }

    public Metadata getMetadata() {
        Metadata metadata = resolveByEntityChain(METADATA);
        if (Objects.isNull(metadata)) {
            return new Metadata();
        }
        return metadata;
    }

    public List<Artifact> getArtifacts() {
        List<Artifact> result = new ArrayList<>();
        for (MappingEntity entity : entityChain) {
            Optional<Entity> artifactsEntity = entity.getChild(ARTIFACTS);
            artifactsEntity.ifPresent(value -> populateArtifacts(result, value));
        }
        return Lists.reverse(result);
    }

    protected void populateArtifacts(List<Artifact> result, Entity entity) {
        Set<Entity> children = entity.getChildren();
        for (Entity child : children) {
            ScalarEntity artifactEntity = (ScalarEntity) child;
            if (Objects.nonNull(artifactEntity.getValue())) {
                Artifact artifact = new Artifact(artifactEntity, getEntity());
                result.add(artifact);
            }
        }
    }

    protected void updateEntityChain(MappingEntity entity) {
        this.entityChain.add(entity);
    }
}

package io.github.edmm.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.support.DescribableElement;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class Operation extends DescribableElement {

    public static final Attribute<Artifact> ARTIFACTS = new Attribute<>("artifacts", Artifact.class);

    private final MappingEntity entity;

    public Operation(MappingEntity operationDefinition, MappingEntity entity) {
        super(operationDefinition);
        this.entity = entity;
    }

    public List<Artifact> getArtifacts() {
        List<Artifact> result = new ArrayList<>();
        Optional<Entity> artifactsEntity = getEntity().getChild(ARTIFACTS);
        artifactsEntity.ifPresent(value -> populateArtifacts(result, value));
        return result;
    }
}

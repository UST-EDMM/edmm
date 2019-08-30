package io.github.edmm.model;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.model.support.BaseElement;
import lombok.ToString;

@ToString
public class Artifact extends BaseElement {

    private final ScalarEntity entity;

    public Artifact(ScalarEntity artifactEntity, MappingEntity entity) {
        super(entity);
        this.entity = artifactEntity;
    }

    @Override
    public String getName() {
        return entity.getName();
    }

    public String getValue() {
        return entity.getValue();
    }
}

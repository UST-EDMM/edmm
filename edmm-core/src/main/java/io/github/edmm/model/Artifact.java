package io.github.edmm.model;

import com.google.common.base.CaseFormat;
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

    public String getNormalizedValue() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, getValue().toLowerCase())
                .replace(".", "_")
                .replace("/", "_")
                .replace("__", "");
    }
}

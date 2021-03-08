package io.github.edmm.model.component;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.visitor.ComponentVisitor;

import lombok.ToString;

@ToString
public class MongoDbSchema extends Database {

    public MongoDbSchema(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public String getSchemaName() {
        return getProperty(SCHEMA_NAME).orElse("data");
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}

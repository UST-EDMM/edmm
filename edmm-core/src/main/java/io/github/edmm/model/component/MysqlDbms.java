package io.github.edmm.model.component;

import io.github.edmm.core.parser.MappingEntity;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class MysqlDbms extends Dbms {

    public MysqlDbms(MappingEntity mappingEntity) {
        super(mappingEntity);
    }
}

package io.github.edmm.model.edimm;

import java.util.List;

import io.github.edmm.model.Operation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RelationInstance extends BasicInstance {
    private String type;
    private String targetInstanceId;
    private List<Operation> operations;
}

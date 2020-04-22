package io.github.edmm.model.edimm;

import java.util.List;

import io.github.edmm.model.Artifact;
import io.github.edmm.model.Operation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ComponentInstance extends BasicInstance {
    private String name;
    private String createdAt;
    private InstanceState.InstanceStateForComponentInstance state;
    private String type;
    private List<Operation> operations;
    private List<Artifact> artifacts;
    private List<RelationInstance> relationInstances;
}

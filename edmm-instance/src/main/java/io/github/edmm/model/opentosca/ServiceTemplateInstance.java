package io.github.edmm.model.opentosca;

import java.util.List;

import javax.xml.namespace.QName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ServiceTemplateInstance {
    QName serviceTemplateId;
    String serviceTemplateInstanceId;
    String createdAt;
    String csarId;
    TOSCAState.ServiceTemplateInstanceState state;
    List<NodeTemplateInstance> nodeTemplateInstances;
}

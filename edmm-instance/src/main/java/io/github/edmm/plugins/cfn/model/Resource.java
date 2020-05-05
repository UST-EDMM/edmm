package io.github.edmm.plugins.cfn.model;

import com.google.gson.internal.LinkedTreeMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Resource {
    LinkedTreeMap<String, Object> DependsOn;
}

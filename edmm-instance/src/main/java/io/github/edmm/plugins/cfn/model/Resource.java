package io.github.edmm.plugins.cfn.model;

import com.google.gson.internal.LinkedTreeMap;
import lombok.Getter;

@Getter
public class Resource {
    LinkedTreeMap<String, Object> DependsOn;
}

package io.github.edmm.plugins.multi.model;

import java.io.Serializable;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentProperties implements Serializable {

    private String component;
    private HashMap<String, String> properties;

}

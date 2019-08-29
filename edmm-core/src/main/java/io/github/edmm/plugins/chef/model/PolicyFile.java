package io.github.edmm.plugins.chef.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PolicyFile {
    private String name;
    private String runningOrder;
    private List<CookBook> cookbooks;
}

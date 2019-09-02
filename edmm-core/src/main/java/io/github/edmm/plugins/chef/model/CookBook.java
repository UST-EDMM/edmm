package io.github.edmm.plugins.chef.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CookBook {
    private String name;
    private String path;
    private List<ShellRecipe> shellRecipes;
}

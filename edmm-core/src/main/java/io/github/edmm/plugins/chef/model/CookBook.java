package io.github.edmm.plugins.chef.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CookBook {
    private String name;
    private String path;
    private List<ShellRecipe> shellRecipes;
}

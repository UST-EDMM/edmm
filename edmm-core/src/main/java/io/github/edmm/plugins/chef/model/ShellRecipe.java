package io.github.edmm.plugins.chef.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ShellRecipe {
    private String name;
    private String fileName;
    private String filePath;
    private String targetPath;
    private String sourcePath;


}

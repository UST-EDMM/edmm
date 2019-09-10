package io.github.edmm.plugins.chef.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShellRecipe {
    private String name;
    private String fileName;
    private String filePath;
    private String targetPath;
    private String sourcePath;
}

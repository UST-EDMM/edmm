package io.github.edmm.core.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginFileAccess {

    private static final Logger logger = LoggerFactory.getLogger(PluginFileAccess.class);

    private final File source;
    private final File target;

    public PluginFileAccess(File source, File target) {
        this.source = source;
        this.target = target;
    }

    public void copy(String relativeSourcePath) throws IOException {
        copy(relativeSourcePath, relativeSourcePath);
    }

    public void copy(String relativeSourcePath, String relativeTargetPath) throws IOException {
        File source = new File(this.source, relativeSourcePath);
        File target = new File(this.target, relativeTargetPath);
        if (!source.exists()) {
            logger.error("Failed to copy '{}': file not found", source);
            throw new FileNotFoundException();
        }
        try {
            if (source.isDirectory()) {
                FileUtils.copyDirectory(source, target);
            } else if (source.isFile()) {
                FileUtils.copyFile(source, target);
            }
        } catch (IOException e) {
            logger.error("Failed to copy from '{}' to '{}'", source, target, e);
            throw e;
        }
    }
}

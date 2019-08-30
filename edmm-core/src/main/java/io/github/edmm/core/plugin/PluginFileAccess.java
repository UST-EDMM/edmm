package io.github.edmm.core.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.github.edmm.utils.Consts;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class PluginFileAccess {

    private static final Logger logger = LoggerFactory.getLogger(PluginFileAccess.class);

    private final File sourceDirectory;
    private final File targetDirectory;

    public PluginFileAccess(File sourceDirectory, File targetDirectory) {
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
        logger.debug("Requesting file access:");
        logger.debug("  > [source] {}", sourceDirectory);
        logger.debug("  > [target] {}", targetDirectory);
    }

    /**
     * Copies a file from the source directory to the target directory
     *
     * @param relativeSourcePath The relative path in the source directory
     * @param relativeTargetPath The relative path in the target directory
     */
    public void copy(String relativeSourcePath, String relativeTargetPath) throws IOException {
        File source = new File(sourceDirectory, relativeSourcePath);
        File target = new File(targetDirectory, relativeTargetPath);
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

    /**
     * Copies a file from the source directory to the same position in the target directory
     *
     * @param relativeSourcePath The relative path in the source directory
     */
    public void copy(String relativeSourcePath) throws IOException {
        copy(relativeSourcePath, relativeSourcePath);
    }

    /**
     * Deletes a file or directory in the target directory
     *
     * @param relativePath The relative path inside the target directory
     * @return {@code true} if the file or directory was deleted, otherwise {@code false}
     */
    public boolean delete(String relativePath) {
        File file = new File(targetDirectory, relativePath);
        if (file.isDirectory()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                logger.error("Failed to delete directory '{}'", file);
                return false;
            }
        } else if (file.isFile()) {
            return FileUtils.deleteQuietly(file);
        }
        return true;
    }

    /**
     * Writes a string to a file creating the file if it does not exist in the target directory
     *
     * @param relativePath The relative path inside the target directory
     * @param data         The string to append
     */
    public void append(String relativePath, String data) throws IOException {
        File file = new File(targetDirectory, relativePath);
        FileUtils.writeStringToFile(file, data + Consts.NL, StandardCharsets.UTF_8, true);
    }

    /**
     * Returns the absolute path of a file in the target directory
     *
     * @param relativePath The relative path inside the target directory
     * @return The absolute path
     */
    public String getAbsolutePath(String relativePath) throws FileNotFoundException {
        File file = new File(targetDirectory, relativePath);
        if (file.exists()) {
            return file.getAbsolutePath();
        } else {
            throw new FileNotFoundException("File not found");
        }
    }
}

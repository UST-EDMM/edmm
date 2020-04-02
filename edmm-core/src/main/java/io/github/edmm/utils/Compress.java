package io.github.edmm.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

public abstract class Compress {

    public static void zip(Path sourceDirectory, Path targetFile) {
        if (!Files.isDirectory(sourceDirectory)) {
            throw new IllegalArgumentException("sourceDirectory must be a directory");
        }
        if (Files.exists(targetFile)) {
            FileUtils.deleteQuietly(targetFile.toFile());
        }
        try {
            Files.createFile(targetFile);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create targetFile", e);
        }
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(targetFile))) {
            Files.walk(sourceDirectory)
                .filter(path -> !Files.isDirectory(path))
                .forEach(Exceptions.rethrow(path -> {
                    ZipEntry zipEntry = new ZipEntry(sourceDirectory.relativize(path).toString());
                    zos.putNextEntry(zipEntry);
                    Files.copy(path, zos);
                    zos.closeEntry();
                }));
        } catch (IOException e) {
            throw new IllegalStateException("Could not create ZIP file", e);
        }
    }
}

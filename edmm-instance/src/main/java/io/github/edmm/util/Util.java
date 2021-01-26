package io.github.edmm.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Util {

    private final static Logger logger = LoggerFactory.getLogger(Util.class);

    public static String readFromFile(String path) {
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            return readFromStream(fileInputStream);
        } catch (IOException e) {
            logger.error("Error while retrieving contents of the file located at: {}", path);
        }
        return "";
    }

    public static String readFromStream(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        bufferedReader.lines().forEach(str -> stringBuilder.append(str).append("\n"));

        return stringBuilder.toString();
    }
}

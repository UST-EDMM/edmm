package io.github.edmm.docker.support;

import java.io.PrintWriter;

public class FromEntry extends DockerfileEntry {

    private final String baseImage;

    public FromEntry(String baseImage) {
        this.baseImage = baseImage;
    }

    @Override
    public void append(PrintWriter pw, DockerfileEntry prev, DockerfileEntry next, boolean compress) {
        pw.println("FROM " + baseImage);
    }
}

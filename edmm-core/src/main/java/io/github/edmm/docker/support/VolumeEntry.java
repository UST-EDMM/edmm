package io.github.edmm.docker.support;

import java.io.PrintWriter;

public class VolumeEntry extends DockerfileEntry {

    private final String path;

    public VolumeEntry(String path) {
        this.path = path;
    }

    @Override
    public void append(PrintWriter pw, DockerfileEntry prev, DockerfileEntry next, boolean compress) {
        pw.println("VOLUME " + path);
    }
}

package io.github.edmm.docker.support;

import java.io.PrintWriter;

public class ExposeEntry extends DockerfileEntry {

    private final int port;

    public ExposeEntry(int port) {
        this.port = port;
    }

    @Override
    public void append(PrintWriter pw, DockerfileEntry prev, DockerfileEntry next, boolean compress) {
        pw.println("EXPOSE " + port);
    }
}

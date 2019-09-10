package io.github.edmm.docker.support;

import java.io.PrintWriter;

public class AddEntry extends DockerfileEntry {

    private final String src;
    private final String dest;

    protected String command;

    public AddEntry(String src, String dest) {
        this.src = src;
        this.dest = dest;
        this.command = "ADD ";
    }

    @Override
    public void append(PrintWriter pw, DockerfileEntry prev, DockerfileEntry next, boolean compress) {
        pw.println(command + src + " " + dest);
    }
}

package io.github.edmm.docker.support;

import java.io.PrintWriter;

public class EnvEntry extends DockerfileEntry {

    private final String name;
    private final String value;

    public EnvEntry(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void append(PrintWriter pw, DockerfileEntry prev, DockerfileEntry next, boolean compress) {
        pw.println("ENV " + name + "=" + value);
    }
}

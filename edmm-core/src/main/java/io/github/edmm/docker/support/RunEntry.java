package io.github.edmm.docker.support;

import java.io.PrintWriter;

public class RunEntry extends DockerfileEntry {

    private final String command;

    public RunEntry(String command) {
        this.command = command;
    }

    @Override
    public void append(PrintWriter pw, DockerfileEntry prev, DockerfileEntry next, boolean compress) {
        if (!compress) {
            pw.println("RUN " + command);
        } else {
            if (!(prev instanceof RunEntry)) {
                pw.print("RUN " + command);
            } else {
                pw.print("    " + command);
            }
            if (next instanceof RunEntry) {
                pw.println(" && \\");
            } else {
                pw.println();
            }
        }
    }
}

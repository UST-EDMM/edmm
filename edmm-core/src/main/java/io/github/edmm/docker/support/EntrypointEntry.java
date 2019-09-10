package io.github.edmm.docker.support;

import java.io.PrintWriter;

public class EntrypointEntry extends DockerfileEntry {

    private final String[] commands;

    protected String command = "ENTRYPOINT ";

    public EntrypointEntry(String... commands) {
        this.commands = commands;
    }

    @Override
    public void append(PrintWriter pw, DockerfileEntry prev, DockerfileEntry next, boolean compress) {
        pw.print(command + "[");
        for (int i = 0; i < commands.length; i++) {
            String cmd = commands[i];
            pw.printf("\"%s\"", cmd);
            if (i + 1 < commands.length) {
                pw.print(", ");
            }
        }
        pw.print("]");
        pw.println();
    }
}

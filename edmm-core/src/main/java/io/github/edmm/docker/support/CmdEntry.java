package io.github.edmm.docker.support;

public class CmdEntry extends EntrypointEntry {

    public CmdEntry(String... commands) {
        super(commands);
        this.command = "CMD ";
    }
}

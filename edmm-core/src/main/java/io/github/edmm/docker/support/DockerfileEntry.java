package io.github.edmm.docker.support;

import java.io.PrintWriter;

public abstract class DockerfileEntry {

    /**
     * Writes the corresponding entry to the given {@link PrintWriter}.
     *
     * @param pw       The PrintWriter to write to
     * @param prev     The previous Dockerfile entry
     * @param next     The next Dockerfile entry
     * @param compress If RUN commands should be compressed to one command using chain execution
     */
    public abstract void append(PrintWriter pw, DockerfileEntry prev, DockerfileEntry next, boolean compress);
}

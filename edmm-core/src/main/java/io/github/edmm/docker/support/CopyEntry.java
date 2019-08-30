package io.github.edmm.docker.support;

public class CopyEntry extends AddEntry {

    public CopyEntry(String src, String dest) {
        super(src, dest);
        this.command = "COPY ";
    }
}

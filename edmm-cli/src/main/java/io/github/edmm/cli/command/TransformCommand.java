package io.github.edmm.cli.command;

import java.io.File;

import picocli.CommandLine;

@CommandLine.Command(
        name = "transform",
        descriptionHeading = "%n",
        description = "Starts a transformation to a target technology",
        customSynopsis = "@|bold edmm transform|@ @|yellow <target>|@ @|yellow <input>|@"
)
public class TransformCommand implements Runnable {

    @CommandLine.Parameters(arity = "1..1", index = "0", description = "The name of the transformation target")
    private String target;

    @CommandLine.Parameters(arity = "1..1", index = "1..*", description = "The input EDMM file (YAML)")
    private File input;

    @Override
    public void run() {

    }
}

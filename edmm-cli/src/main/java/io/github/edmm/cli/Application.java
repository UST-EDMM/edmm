package io.github.edmm.cli;

import io.github.edmm.cli.command.TransformCommand;
import org.fusesource.jansi.AnsiConsole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

import static picocli.CommandLine.usage;

@CommandLine.Command(
        name = "edmm",
        header = "@|bold,green Essential Deployment Metamodel Command Line Interface%n|@",
        customSynopsis = "@|bold edmm|@ [@|yellow <command>|@] [@|yellow <subcommand>|@]",
        footer = {
                "%nSee 'edmm help [@|yellow <command>|@]' for detailed help information"
        },
        subcommands = {
                CommandLine.HelpCommand.class,
                TransformCommand.class
        }
)
@SpringBootApplication
public class Application implements CommandLineRunner, Runnable {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        AnsiConsole.systemInstall();
        System.setProperty("picocli.ansi", "true");
        CommandLine cmd = new CommandLine(this);
        int exitCode = cmd.execute(args);
        AnsiConsole.systemUninstall();
        System.exit(exitCode);
    }

    @Override
    public void run() {
        usage(this, System.out);
    }
}

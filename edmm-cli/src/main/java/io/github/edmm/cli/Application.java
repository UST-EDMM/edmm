package io.github.edmm.cli;

import io.github.edmm.cli.command.TransformCommand;
import org.fusesource.jansi.AnsiConsole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
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
public class Application implements CommandLineRunner, Runnable, ExitCodeGenerator {

    private int exitCode = 0;

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        System.setProperty("picocli.ansi", "true");
        SpringApplication.run(Application.class, args);
        AnsiConsole.systemUninstall();
    }

    @Override
    public void run(String... args) {
        CommandLine cmd = new CommandLine(this);
        exitCode = cmd.execute(args);
    }

    @Override
    public void run() {
        usage(this, System.out);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}

package io.github.edmm.cli;

import org.fusesource.jansi.AnsiConsole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
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
        TransformCommand.class,
        DeployCommand.class,
    }
)
@SpringBootApplication(scanBasePackages = "io.github.edmm")
@ImportResource( {"classpath*:pluginContext.xml"})
public class Application implements CommandLineRunner, Runnable, ExitCodeGenerator {

    public static final String PICOCLI_ANSI = "picocli.ansi";

    private final CommandLine.IFactory factory;

    private int exitCode = 0;

    @Autowired
    public Application(CommandLine.IFactory factory) {
        this.factory = factory;
    }

    @Override
    public void run(String... args) {
        exitCode = new CommandLine(this, factory).execute(args);
    }

    @Override
    public void run() {
        usage(this, System.out);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        System.setProperty(PICOCLI_ANSI, String.valueOf(true));
        int exitCode = SpringApplication.exit(SpringApplication.run(Application.class, args));
        AnsiConsole.systemUninstall();
        System.exit(exitCode);
    }
}

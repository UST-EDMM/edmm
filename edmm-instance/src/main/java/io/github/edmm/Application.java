package io.github.edmm;

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
    name = "edmmi",
    header = "@|bold,green Essential Deployment Metamodel instance Command Line Interface%n|@",
    customSynopsis = "@|bold edmmi|@ [@|yellow <command>|@] [@|yellow <subcommand>|@]",
    footer = {
        "%nSee 'edmmi help [@|yellow <command>|@]' for detailed help information"
    },
    subcommands = {
        CommandLine.HelpCommand.class,
        TransformCommand.class,
        ParseCommand.class
    }
)
@SpringBootApplication(scanBasePackages = "io.github.edmm")
@ImportResource({"classpath*:instancePluginContext.xml"})
public class Application implements CommandLineRunner, Runnable, ExitCodeGenerator {

    private static final String PICOCLI_ANSI = "picocli.ansi";

    private final CommandLine.IFactory factory;

    private int exitCode = 0;

    @Autowired
    public Application(CommandLine.IFactory factory) {
        this.factory = factory;
    }

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        System.setProperty(PICOCLI_ANSI, String.valueOf(true));
        int exitCode = SpringApplication.exit(SpringApplication.run(Application.class, args));
        AnsiConsole.systemUninstall();
        System.exit(exitCode);
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
}

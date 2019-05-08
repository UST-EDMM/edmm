package io.github.ust.edmm;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

import static picocli.CommandLine.usage;

@CommandLine.Command(
        name = "edm",
        header = "@|bold,green Essential Deployment Model Command Line Interface%n|@",
        customSynopsis = "@|bold edm|@ [@|yellow <command>|@] [@|yellow <subcommand>|@]"
)
@SpringBootApplication
public class Application implements CommandLineRunner, Runnable {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        CommandLine.run(this, args);
    }

    @Override
    public void run() {
        usage(this, System.out);
    }
}

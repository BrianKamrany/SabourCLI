package demo;

import javax.inject.Inject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@SpringBootApplication
public class Application implements CommandLineRunner {
	@Inject private CommandLineProcessor cliProcessor;
	@Inject private IFactory picoCLISpringFactory;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		CommandLine picoCLI = new CommandLine(cliProcessor, picoCLISpringFactory);
		picoCLI.execute(args);
	}
}

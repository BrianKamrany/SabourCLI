package demo;

import org.springframework.stereotype.Component;

import lombok.Data;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Component
@Command(name = "")
@Data
public class CommandLineProcessor implements Runnable {
    @Spec private CommandSpec commandSpecification;
    
	@Override
	public void run() {
		commandSpecification.commandLine().usage(System.out);
	}

    @Command(name = "start") 
    public void startBackup() {
    	System.out.println("Start");
    }

    @Command(name = "show", aliases = "list") 
    public void showRelationships() {
    	System.out.println("LIst or show");
    }

    @Command(name = "add") 
    public void addRelationship(@Option(names = "-original", required = true) String original, @Option(names = "-mirror", required = true) String mirror, @Option(names = "-options") String options) {
    	System.out.println("ADD");
    	System.out.println(original);
    	System.out.println(mirror);
    	System.out.println(options);
    }

    @Command(name = "stats") 
    public void calculateStatistics() {
    	System.out.println("Stats");
    }

    @Command(name = "recover") 
    public void recoverFromMirrors() {
    	System.out.println("Recover");
    }
}
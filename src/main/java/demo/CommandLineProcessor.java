package demo;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Component
@Command(name = "")
@Data
@Slf4j
public class CommandLineProcessor implements Runnable {
	@Inject private MirrorMaker mirrorMaker;
	@Inject private LinkService linkService;
    @Spec private CommandSpec commandSpecification;
    
	@Override
	public void run() {
		commandSpecification.commandLine().usage(System.out);
	}

    @Command(name = "start")
    public void createMirrors() throws Exception {
		logger.info("Command: start");
    	mirrorMaker.synchronize();
		logger.info("Mirrors were made successfully.");
    }

    @Command(name = "show", aliases = "list")
    public void showLinks() throws Exception {
		logger.info("Command: show");
    	linkService.showLinks();
    }

    @Command(name = "add")
    public void addLink(
    		@Option(names = "-original", required = true) String original, 
    		@Option(names = "-mirror", required = true) String mirror, 
    		@Option(names = "-bidirectional") boolean bidirectional/*, 
    		@Option(names = "-options") String options*/) throws Exception {
		logger.info("Command: add");
    	linkService.addLink(original, mirror, bidirectional);
    }

    @Command(name = "remove")
    public void removeLink(@Parameters(index = "0") int position) throws Exception {
		logger.info("Command: remove");
    	linkService.removeLink(position);
    }

    @Command(name = "stats")
    public void calculateStatistics() {
		logger.info("Command: statistics");
    	System.out.println("Stats");
    }

    @Command(name = "recover")
    public void recoverFromMirrors() {
		logger.info("Command: recover");
    	System.out.println("Recover");
    }
}

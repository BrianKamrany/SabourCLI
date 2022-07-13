package demo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Component
@Command(name = "")
@Data
@Slf4j
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
    public void showLinks() {
    	System.out.println("LIst or show");
    }

    @Command(name = "add") 
    public void addLink(
    		@Option(names = "-original", required = true) String original, 
    		@Option(names = "-mirror", required = true) String mirror, 
    		@Option(names = "-bidirectional") boolean bidirectional/*, 
    		@Option(names = "-options") String options*/) throws Exception {
    	logger.info("Adding link.");
    	
    	ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
    	
		Path linksPath = Paths.get("links.txt");
    	Links links = new Links();
		if (Files.exists(linksPath)) {
			links = jsonMapper.readValue(linksPath.toFile(), Links.class);
		}
		
		Link link = new Link();
		link.setOriginal(Paths.get(original));
		link.setMirror(Paths.get(mirror));
		link.setRelationship(!bidirectional ? Relationship.STANDARD : Relationship.BIDIRECTIONAL);
		links.add(link);

		if (Files.notExists(linksPath))
			logger.info("Creating links file.");
		jsonMapper.writeValue(linksPath.toFile(), links);
    }

    @Command(name = "remove") 
    public void removeLink() {
    	System.out.println("Remove");
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

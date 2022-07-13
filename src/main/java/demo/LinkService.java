package demo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LinkService {
	private static final Path LINKS_PATH = Paths.get("links.txt");
	
	private ObjectMapper jsonMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
	
	public void addLink(String original, String mirror, boolean bidirectional) throws Exception {
    	logger.info("Adding link.");
    	
    	Links links = new Links();
		if (Files.exists(LINKS_PATH)) {
			links = jsonMapper.readValue(LINKS_PATH.toFile(), Links.class);
		}
		
		Link link = new Link();
		link.setOriginal(Paths.get(original));
		link.setMirror(Paths.get(mirror));
		link.setRelationship(!bidirectional ? Relationship.STANDARD : Relationship.BIDIRECTIONAL);
		links.add(link);

		if (Files.notExists(LINKS_PATH))
			logger.info("Creating links file.");
		jsonMapper.writeValue(LINKS_PATH.toFile(), links);
	}
}

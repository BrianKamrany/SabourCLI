package demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LinkService {
	private static final Path LINKS_PATH = Paths.get("links.txt");
	
	private ObjectMapper jsonMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
	
	public void addLink(String original, String mirror, boolean bidirectional) throws StreamReadException, DatabindException, IOException {
    	logger.info("Adding link.");

		Links links = readLinks();
		
		Link link = new Link();
		link.setOriginal(Paths.get(original));
		link.setMirror(Paths.get(mirror));
		link.setRelationship(!bidirectional ? Relationship.STANDARD : Relationship.BIDIRECTIONAL);
		links.add(link);

		if (Files.notExists(LINKS_PATH))
			logger.info("Creating links file.");
		writeLinks(links);
	}

	public void showLinks() throws StreamReadException, DatabindException, IOException {
		logger.info("Showing links.");
		
		Links links = readLinks();
		
		List<Link> list = links.asList();
		for (int i = 0; i < list.size(); i++) {
			Link link = list.get(i);
			
			System.out.println(i + 1);
			System.out.println(link.getOriginal());
			System.out.println(link.getMirror());
			System.out.println();
		}
	}

	public void removeLink(int position) throws StreamReadException, DatabindException, IOException {
		logger.info("Removing link.");
		
		Links links = readLinks();
		List<Link> list = links.asList();
		int index = position - 1;
		list.remove(index);
		
		writeLinks(links);
	}

	public Links readLinks() throws StreamReadException, DatabindException, IOException {
    	Links links = new Links();
		if (Files.exists(LINKS_PATH)) {
			links = jsonMapper.readValue(LINKS_PATH.toFile(), Links.class);
		}
		return links;
	}

	private void writeLinks(Links links) throws IOException, StreamWriteException, DatabindException {
		jsonMapper.writeValue(LINKS_PATH.toFile(), links);
	}
}

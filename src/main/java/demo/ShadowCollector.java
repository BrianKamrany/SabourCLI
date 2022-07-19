package demo;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ShadowCollector {
	@Inject private LinkService linkService;

	public void collect() throws StreamReadException, DatabindException, IOException {
		logger.info("Collecting shadows.");
		
		Links links = linkService.readLinks();
		
		List<Link> list = links.asList();
		for (int i = 0; i < list.size(); i++) {
			Link link = list.get(i);

			logger.info("Mirror: {}", link.getMirror());
			CollectVisitor visitor = new CollectVisitor(link.getOriginal(), link.getMirror());
			Files.walkFileTree(link.getMirror(), visitor);
		}
	}
}

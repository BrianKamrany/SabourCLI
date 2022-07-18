package demo;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import demo.statistics.Statistics;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StatisticsService {
	@Inject private LinkService linkService;

	public Statistics calculateStatistics() throws StreamReadException, DatabindException, IOException {
		logger.info("Calculating statistics.");
		
		Links links = linkService.readLinks();
		
		List<Link> list = links.asList();
		Statistics stats = new Statistics();
		for (int i = 0; i < list.size(); i++) {
			Link link = list.get(i);

			logger.info("Original: {}", link.getOriginal());
			StatisticsVisitor visitor = new StatisticsVisitor(link.getOriginal(), link.getMirror(), stats);
			Files.walkFileTree(link.getOriginal(), visitor);
		}
		
		return stats;
	}
}

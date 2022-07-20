package demo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import com.google.common.base.Stopwatch;

import demo.statistics.Statistics;
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
	@Inject private StatisticsService statsService;
	@Inject private ShadowCollector shadowCollector;
	@Inject private LinkService linkService;
	
    @Spec private CommandSpec commandSpecification;
    
	@Override
	public void run() {
		commandSpecification.commandLine().usage(System.out);
	}

    @Command(name = "mirror", aliases = {"copy", "clone"})
    public void createMirrors() throws Exception {
		logger.info("Command: mirror");
		Stopwatch timer = Stopwatch.createStarted();
    	mirrorMaker.synchronize();
		timer.stop();
		logger.info("Execution time: {}", timer);
		writeLastBackupToFile();
    }

    @Command(name = "stats", aliases = "statistics")
    public void calculateStatistics() throws Exception {
		logger.info("Command: statistics");
		Statistics stats = statsService.calculateStatistics();
		logger.info(stats.toString());
    }

    @Command(name = "delete", aliases = {"clean", "collect"})
    public void delete() throws Exception {
		logger.info("Command: delete");
		shadowCollector.collect();
    }

    @Command(name = "start")
    public void start() throws Exception {
		logger.info("Command: start");
		calculateStatistics();
		Thread.sleep(4000);
		delete();
		createMirrors();
    }

    @Command(name = "status")
    public void status() throws Exception {
		logger.info("Command: status");
		Date date = readLastBackupFromFile();
		String lastBackupMessage = date != null ? "Last Backup: " + date.toString() : "A backup has not been made.";
		logger.info(lastBackupMessage);
    }

    /*@Command(name = "recover")
    public void recoverFromMirrors() {
		logger.info("Command: recover");
    }*/

    @Command(name = "add")
    public void addLink(
    		@Option(names = "-original", required = true) String original, 
    		@Option(names = "-mirror", required = true) String mirror/*, 
    		@Option(names = "-bidirectional") boolean bidirectional, 
    		@Option(names = "-options") String options*/) throws Exception {
		logger.info("Command: add");
    	linkService.addLink(original, mirror);
    }

    @Command(name = "remove")
    public void removeLink(@Parameters(index = "0") int position) throws Exception {
		logger.info("Command: remove");
    	linkService.removeLink(position);
    }

    @Command(name = "show", aliases = "list")
    public void showLinks() throws Exception {
		logger.info("Command: show");
    	linkService.showLinks();
    }

	private void writeLastBackupToFile() throws IOException {
		File file = new File("last_backup.txt");
		FileUtils.writeStringToFile(file, String.valueOf(new Date().getTime()), StandardCharsets.UTF_8);
	}

	private Date readLastBackupFromFile() throws IOException, ParseException {
		if (!Files.exists(Paths.get("last_backup.txt")))
			return null;
		
		File file = new File("last_backup.txt");
		String lastBackup = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		Date date = new Date(Long.parseLong(lastBackup));
		return date;
	}
}

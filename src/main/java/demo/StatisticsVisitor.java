package demo;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import demo.statistics.Statistics;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatisticsVisitor implements FileVisitor<Path> {
	private Path original;
	private Path mirror;
	private Statistics stats;

	public StatisticsVisitor(Path original, Path mirror, Statistics stats) {
		this.original = original;
		this.mirror = mirror;
		this.stats = stats;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path source, BasicFileAttributes unused) throws IOException {
		Path reflection = SynchronizeVisitor.reflect(original, source, mirror);
		if (!SynchronizeVisitor.isSystem(source) && !Files.exists(reflection, NOFOLLOW_LINKS)) {
			stats.incrementCount();
		}
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path source, BasicFileAttributes attributes) throws IOException {
		Path reflection = SynchronizeVisitor.reflect(original, source, mirror);
		if (!SynchronizeVisitor.isSystem(source) && !SynchronizeVisitor.isMirrored(source, reflection, true)) {
			stats.incrementCount();
			stats.addBytes(attributes.size());
		}
		return CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path source, IOException ex) throws IOException {
		if (ex != null) {
			logger.warn("Failed to visit directory: {}", source);
			logger.warn("", ex);
			return CONTINUE;
		}
		/*Path reflection = SynchronizeVisitor.reflect(original, source, mirror);
		if (Files.exists(reflection, NOFOLLOW_LINKS)) {
			stats.incrementCount();
		}*/
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path source, IOException ex) {
		logger.warn("Failed to visit file: {}", source);
		logger.warn("", ex);
		return CONTINUE;
	}
}

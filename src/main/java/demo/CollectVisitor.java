package demo;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CollectVisitor extends SimpleFileVisitor<Path> {
	private Path original;
	private Path mirror;

	public CollectVisitor(Path original, Path mirror) {
		this.original = original;
		this.mirror = mirror;
	}

	@Override
	public FileVisitResult visitFile(Path reflection, BasicFileAttributes unused) throws IOException {
		if (SynchronizeVisitor.isSystem(reflection)) {
			logger.warn("Sys file: {}", reflection.getFileName());
			return CONTINUE;
		}
		
		Path source = SynchronizeVisitor.reflect(mirror, reflection, original);
		if (!SynchronizeVisitor.isSystem(reflection) && Files.notExists(source, NOFOLLOW_LINKS)) {
			logger.info("File: {}", reflection.getFileName());
			Files.delete(reflection);
		}
		return CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path reflection, IOException ex) throws IOException {
		if (SynchronizeVisitor.isSystem(reflection)) {
			logger.warn("Sys dir: {}", reflection.getFileName());
			return CONTINUE;
		}
		if (ex != null) {
			logger.warn("Failed to visit directory: {}", reflection);
			logger.warn("", ex);
			return CONTINUE;
		}
		Path source = SynchronizeVisitor.reflect(mirror, reflection, original);
		if (!SynchronizeVisitor.isSystem(reflection) && Files.notExists(source, NOFOLLOW_LINKS)) {
			logger.info("Dir: {}", reflection.getFileName());
			Files.delete(reflection);
		}
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path source, IOException ex) {
		logger.warn("Failed to visit file: {}", source);
		logger.warn("", ex);
		return CONTINUE;
	}
}

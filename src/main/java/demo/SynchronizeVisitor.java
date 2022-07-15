package demo;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynchronizeVisitor implements FileVisitor<Path> {
	private Path original;
	private Path mirror;
	private Path temporary;
	private byte[] copyBuffer = new byte[8 * 1024];

	public SynchronizeVisitor(Path original, Path mirror) {
		this.original = original;
		this.mirror = mirror;
		this.temporary = Paths.get(mirror.toString(), "Sabour.temp");
	}

	@Override
	public FileVisitResult preVisitDirectory(Path source, BasicFileAttributes unused) throws IOException {
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path source, BasicFileAttributes unused) throws IOException {
		Path reflection = toReflection(original, source, mirror);
		if (!Files.exists(reflection, LinkOption.NOFOLLOW_LINKS)) {
			logger.info("Synchronizing: {}", reflection);
			copyFile(source, temporary);
			Files.move(temporary, reflection, REPLACE_EXISTING);
		}
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path source, IOException ex) {
		logger.warn("Failed to visit file: {}", source);
		logger.warn("", ex);
		return CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path source, IOException ex) {
		logger.warn("Failed to visit path: {}", ex);
		logger.warn("", ex);
		return CONTINUE;
	}
	
	private Path toReflection(Path original, Path source, Path mirror) {
		return mirror.resolve(original.relativize(source));
	}

	// Using "Custom Buffer Buffered Stream" approach given by (https://baptiste-wicht.com/posts/2010/08/file-copy-in-java-benchmark.html).
	private void copyFile(Path source, Path reflection) throws IOException {
		try (
			InputStream inputStream = new BufferedInputStream(new FileInputStream(source.toFile()));
			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(reflection.toFile()));
		) {
			while (true) {
				int bytes = inputStream.read(copyBuffer);
				if (bytes == -1)
					break;
				outputStream.write(copyBuffer, 0, bytes);
			}
		}
	}
}

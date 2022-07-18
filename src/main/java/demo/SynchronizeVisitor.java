package demo;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;

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
		Path reflection = toReflection(original, source, mirror);
		if (!isSystem(source) && !Files.exists(reflection, NOFOLLOW_LINKS)) {
			logger.info("Synchronizing directory: {}", reflection);
			Files.createDirectory(reflection);
		}
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path source, BasicFileAttributes unused) throws IOException {
		Path reflection = toReflection(original, source, mirror);
		if (!isSystem(source) && !isMirrored(source, reflection, true)) {
			logger.info("Synchronizing file: {}", reflection);
			copyFile(source, temporary);
			Files.move(temporary, reflection, REPLACE_EXISTING);
			copyAttributes(source, reflection);
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
		Path reflection = toReflection(original, source, mirror);
		if (Files.exists(reflection, NOFOLLOW_LINKS)) {
			copyAttributes(source, reflection);
		}
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path source, IOException ex) {
		logger.warn("Failed to visit file: {}", source);
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

	private void copyAttributes(Path source, Path reflection) throws IOException {
			DosFileAttributeView sourceView = Files.getFileAttributeView(source, DosFileAttributeView.class, NOFOLLOW_LINKS);
			DosFileAttributeView reflectionView = Files.getFileAttributeView(reflection, DosFileAttributeView.class, NOFOLLOW_LINKS);
			DosFileAttributes sourceAttributes = sourceView.readAttributes();
			//reflectionView.setArchive(sourceAttributes.isArchive());
			//reflectionView.setHidden(sourceAttributes.isHidden());
			//reflectionView.setReadOnly(sourceAttributes.isReadOnly());
			//reflectionView.setSystem(sourceAttributes.isSystem());
			reflectionView.setTimes(sourceAttributes.lastModifiedTime(), sourceAttributes.lastAccessTime(), sourceAttributes.creationTime());
	}
	
	private boolean isMirrored(Path source, Path reflection, boolean file) throws IOException {
		if (!Files.exists(reflection, NOFOLLOW_LINKS))
			return false;

		DosFileAttributeView sourceView = Files.getFileAttributeView(source, DosFileAttributeView.class, NOFOLLOW_LINKS);
		DosFileAttributeView reflectionView = Files.getFileAttributeView(reflection, DosFileAttributeView.class, NOFOLLOW_LINKS);
		DosFileAttributes sourceAttributes = sourceView.readAttributes();
		DosFileAttributes reflectionAttributes = reflectionView.readAttributes();
		
		return sourceAttributes.creationTime().equals(reflectionAttributes.creationTime())
			//&& sourceAttributes.lastAccessTime().equals(reflectionAttributes.lastAccessTime())
			&& sourceAttributes.lastModifiedTime().equals(reflectionAttributes.lastModifiedTime())
			&& (file ? sourceAttributes.size() == reflectionAttributes.size() : true);
			//&& sourceAttributes.isArchive() == reflectionAttributes.isArchive()
			//&& sourceAttributes.isHidden() == reflectionAttributes.isHidden()
			//&& sourceAttributes.isReadOnly() == reflectionAttributes.isReadOnly()
			//&& sourceAttributes.isSystem() == reflectionAttributes.isSystem()
	}

	public boolean isSystem(Path path) throws IOException {
		DosFileAttributeView pathView = Files.getFileAttributeView(path, DosFileAttributeView.class, NOFOLLOW_LINKS);
		DosFileAttributes pathAttributes = pathView.readAttributes();
		return pathAttributes.isSystem();
	}
}

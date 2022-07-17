package demo;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintVisitor implements FileVisitor<Path> {
	private Path original;
	private Path mirror;

	public PrintVisitor(Path original, Path mirror) {
		this.original = original;
		this.mirror = mirror;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path source, BasicFileAttributes unused) throws IOException {
		try {
			Path reflection = toReflection(original, source, mirror);
			if (isSystem(source)) {
				logger.info("SYSTEM DIRECTORY: {}", source.getFileName());
			}
			if (!Files.exists(reflection, NOFOLLOW_LINKS)) {
				//logger.info("Synchronizing directory: {}", reflection);
			}
		} catch (Exception ex) {
			logger.warn("Failedxxx.");
			logger.warn("", ex);
			return FileVisitResult.TERMINATE;
		}
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path source, BasicFileAttributes unused) throws IOException {
		Path reflection = toReflection(original, source, mirror);
		if (isSystem(source)) {
			logger.info("SYSTEM FILE: {}", source.getFileName());
		}
		if (!isMirrored(source, reflection, true)) {
			//logger.info("Synchronizing file: {}", reflection);
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
	
	private boolean isMirrored(Path source, Path reflection, boolean file) throws IOException {
		if (!Files.exists(reflection, NOFOLLOW_LINKS))
			return false;

		DosFileAttributeView sourceView = Files.getFileAttributeView(source, DosFileAttributeView.class, NOFOLLOW_LINKS);
		DosFileAttributeView reflectionView = Files.getFileAttributeView(reflection, DosFileAttributeView.class, NOFOLLOW_LINKS);
		DosFileAttributes sourceAttributes = sourceView.readAttributes();
		DosFileAttributes reflectionAttributes = reflectionView.readAttributes();
		
		return sourceAttributes.creationTime().equals(reflectionAttributes.creationTime())
			&& sourceAttributes.lastAccessTime().equals(reflectionAttributes.lastAccessTime())
			&& sourceAttributes.lastModifiedTime().equals(reflectionAttributes.lastModifiedTime())
			&& (file ? sourceAttributes.size() == reflectionAttributes.size() : true);
			//&& sourceAttributes.isArchive() == reflectionAttributes.isArchive()
			//&& sourceAttributes.isHidden() == reflectionAttributes.isHidden()
			//&& sourceAttributes.isReadOnly() == reflectionAttributes.isReadOnly()
			//&& sourceAttributes.isSystem() == reflectionAttributes.isSystem()
	}

	public static boolean isSystem(Path path) throws IOException {
		DosFileAttributeView pathView = Files.getFileAttributeView(path, DosFileAttributeView.class, NOFOLLOW_LINKS);
		DosFileAttributes pathAttributes = pathView.readAttributes();
		return pathAttributes.isSystem();
	}
}

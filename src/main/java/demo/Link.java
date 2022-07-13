package demo;

import java.nio.file.Path;

import lombok.Data;

@Data
public class Link {
	private Path original;
	private Path mirror;
	private Relationship relationship;
	//private boolean delete;
	//private boolean readOnly;
	//private boolean system;
}

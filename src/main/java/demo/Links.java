package demo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Links {
	private List<Link> links = new ArrayList<>();
	
	public void add(Link link) {
		links.add(link);
	}
	
	public List<Link> toList() {
		return new ArrayList<>(links);
	}
}

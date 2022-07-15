package demo.statistics;

public class Statistics {
	private int count;
	private long bytes;
	
	public void incrementCount() {
		count++;
	}
	
	public void addBytes(long bytes) {
		this.bytes += bytes;
	}

	public boolean isEmpty() {
		return count == 0 && bytes == 0;
	}

	public void combine(Statistics stats) {
		this.count += stats.count;
		this.bytes += stats.bytes;
	}
	
	//equals/hashcode
}

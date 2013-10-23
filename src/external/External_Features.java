package external;

import java.io.Serializable;

public class External_Features implements Serializable {
	String fileName;
	int totalImageCount;
	int tableCount;
	int totalWordCount;
	double timeToDeadLine;
	double averageWordLength;
	int wordsOverAverageWordLength;
	public String getFileName() {
		return fileName;
	}
	public int getTotalImageCount() {
		return totalImageCount;
	}
	public int getTableCount() {
		return tableCount;
	}
	public int getTotalWordCount() {
		return totalWordCount;
	}
	public double getTimeToDeadLine() {
		return timeToDeadLine;
	}
	public double getAverageWordLength() {
		return averageWordLength;
	}
	public int getWordsOverAverageWordLength() {
		return wordsOverAverageWordLength;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setTotalImageCount(int totalImageCount) {
		this.totalImageCount = totalImageCount;
	}
	public void setTableCount(int tableCount) {
		this.tableCount = tableCount;
	}
	public void setTotalWordCount(int totalWordCount) {
		this.totalWordCount = totalWordCount;
	}
	public void setTimeToDeadLine(double timeToDeadLine) {
		this.timeToDeadLine = timeToDeadLine;
	}
	public void setAverageWordLength(double averageWordLength) {
		this.averageWordLength = averageWordLength;
	}
	public void setWordsOverAverageWordLength(int wordsOverAverageWordLength) {
		this.wordsOverAverageWordLength = wordsOverAverageWordLength;
	}
	
}

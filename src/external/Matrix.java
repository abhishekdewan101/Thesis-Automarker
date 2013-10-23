package external;

import java.io.Serializable;
import java.util.ArrayList;

public class Matrix implements Serializable {
	ArrayList<String> words = new ArrayList<String>();
	ArrayList<String> documentList = new ArrayList<String>();
	double[][] countMatrix;
	
	public Matrix(int noOfWords, int noOfDocuments){
		countMatrix = new double[noOfWords][noOfDocuments];
	}
	
	public void addWord(String word){
		words.add(word);
	}
	
	public void addDocument(String document){
		documentList.add(document);
	}
	
	public void increaseCount(int row,int column){
		countMatrix[row][column]++;
	}
	
	public int getWord(String word){
		return words.indexOf(word);
	}
	
	public int getDocument(String document){
		return documentList.indexOf(document);
	}
	
	public boolean isWordContained(String word){
		return words.contains(word);
	}
	
	public boolean isDocumentContained(String document){
		return documentList.contains(document);
	}
}

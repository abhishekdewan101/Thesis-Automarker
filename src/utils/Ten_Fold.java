package utils;

import java.io.File;

public class Ten_Fold {
	String inputDirectory;
	File[][] foldFiles;
	File[] textFiles;
	int [] foldIndex = new int[10];
	
	//-----CONSTRUCTOR--------------
	
	public Ten_Fold(String inputTextDirectory){
		inputDirectory = inputTextDirectory;
		execute();
	}
	
	//-----METHODS------------------
	
	public void execute(){
		createTenFold();
	}

	private void createTenFold() {
		textFiles = new File(inputDirectory).listFiles();
		int filesPerFold;
		
		if(textFiles.length%10==0){
			filesPerFold = textFiles.length/10;
		}else{
			filesPerFold = textFiles.length/10 + 1;
		}
		
		foldFiles = new File[10][filesPerFold];
		int fold;
		for(int i=0;i<textFiles.length;i++){
			fold = i%10;
			foldFiles[fold][foldIndex[fold]] = textFiles[i];
			foldIndex[fold]++;
		}
	}
	
	public int getLength(int trainingSetNumber){
		int length = textFiles.length - foldIndex[trainingSetNumber];
		return length;
	}
	
	public File[][] getFoldFiles() {
		return foldFiles;
	}

	public int[] getFoldIndex() {
		return foldIndex;
	}
}

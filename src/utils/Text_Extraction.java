package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class Text_Extraction {
	String inputDirectory;
	String outputDirectory;
	boolean singularFile;
	
	//----------CONSTRUCTOR----------------
	public Text_Extraction(String inDirectory,String outDirectory,boolean singleFile){
		inputDirectory = inDirectory;
		outputDirectory = outDirectory;
		singularFile = singleFile;
		executeSteps();
	}
	
	//----------METHODS--------------------
	public void executeSteps(){
		convertToText();
	}
	
	public void convertToText(){
		File[] thesisFiles;
		PdfReader reader;
		
		thesisFiles = new File(inputDirectory).listFiles();
		for(int i=0;i<thesisFiles.length;i++){
			if(!thesisFiles[i].getName().contains(".DS_Store")){
				System.out.println("[PDF CONVERSION]		"+thesisFiles[i].getName());
			    try{
			    	reader = new PdfReader(thesisFiles[i].getAbsolutePath());
			    	File tmpTextFile = new File(outputDirectory+"/"+thesisFiles[i].getName().substring(0, 7)+".txt");
			    	
			    	if(!tmpTextFile.exists()){
			    		tmpTextFile.createNewFile();   //if file doesn't exsist create new.
			    	}else{
			    		tmpTextFile.delete();		  // delete if file exsists then create new.
			    		tmpTextFile.createNewFile();
			    	}
			    	
			        for(int j = 1;j <= reader.getNumberOfPages();j++){
			            String str=PdfTextExtractor.getTextFromPage(reader, j); //Extracting the content from a particular page.
	        	        FileWriter fileWritter = new FileWriter(tmpTextFile,true);
	        		    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        		    bufferWritter.write(str);
	        		    bufferWritter.close();
	   				}
			    }catch(IOException e){
			    	e.printStackTrace();
			    }
			}
		}
		System.out.println("[PDF CONVERSION]		"+"Conversion Done");
	}
}

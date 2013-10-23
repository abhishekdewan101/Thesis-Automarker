import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import learners.LSA;
import learners.Mark_Regression;
import learners.Summary_Regression;
import learners.Word_Regression;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import plot.scatter;
import utils.Ten_Fold;
import utils.Text_Extraction;


public class execute {
	public static void main(String[] args){
		String textFileDirectory = "textFiles";
		Text_Extraction te = new Text_Extraction("theses","textFiles",false);
		setup s = new setup("automark1");
		Ten_Fold tf = new Ten_Fold(textFileDirectory);
		File[][] foldFiles = tf.getFoldFiles();
		int[] index = tf.getFoldIndex(); 
		
		double[] finalActualMarks = new double[getSize(index)];
		double[] finalPredictedMarks = new double[getSize(index)];
		int counter=0;
		
		int finalTestingFold =0;
		int testingFold =0;
		for(int i =0;i<10;i++){
			testingFold =i;
			finalTestingFold = (i+1==10)?0:i+1;
			
			
			System.out.println("Running Summary Regression");
			Summary_Regression sr = new Summary_Regression(foldFiles,index,testingFold,finalTestingFold);
			double[] coeff = sr.getCoff();
			String[] variables = {"totalImageCount","totalTableCount","totalWordCount","submissionTimeLeft","averageWordLength","wordsOverAverageWordLength"};
			try
			{
			    String filename= "summaryRegressionEquation.txt";
			    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
			    fw.write("mark = "+coeff[0]+" + ");
			    for(int j=1;j<coeff.length;j++){
			    	if(j==coeff.length - 1){
			    	fw.write(coeff[j]+"\n");	
			    	}else{
			    	fw.write(coeff[j]+" + ");	
			    	}
				}
			    fw.close();
			}
			catch(IOException ioe)
			{
			    System.err.println("IOException: " + ioe.getMessage());
			}
			
			double[] actualMark = sr.getActualMarks();
			double[] predictedMark = sr.getPredictedMarks();
//			
			
//			//--------------TESTING FOR SUMMARY REGRESSION-----------------
			for(int j=0;j<actualMark.length;j++){
				System.out.println(actualMark[j]+"	"+predictedMark[j]);
				try
				{
				    String filename= "summaryRegression.csv";
				    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
				    fw.write(actualMark[j]+","+predictedMark[j]+"\n");
				    fw.close();
				}
				catch(IOException ioe)
				{
				    System.err.println("IOException: " + ioe.getMessage());
				}
			}
//			-------------------------------------------------------------
			for(int j=0;j<actualMark.length;j++){
				System.out.println(actualMark[j] +"		"+predictedMark[j]);
			}
			
			double[] finalActualMark = sr.getFinalActualMarks();
			double[] finalPredictedMark = sr.getFinalPredictedMarks();
			for(int j=0;j<finalActualMark.length;j++){
				System.out.println(finalActualMark[j] +"		"+finalPredictedMark[j]);
			}
			
			System.out.println("Running Word Regression");
			Word_Regression wr = new Word_Regression(foldFiles,index,testingFold,finalTestingFold);
			double[] actualMark1 = wr.getActualMarks();
			double[] predictedMark1 = wr.getPredictedMarks();
			for(int j=0;j<actualMark1.length;j++){
				System.out.println(actualMark1[j] +"		"+predictedMark1[j]);
			}
//		
//			//--------------TESTING FOR WORD REGRESSION-----------------
			for(int j=0;j<actualMark1.length;j++){
				try
				{
				    String filename= "wordRegression.csv";
				    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
				    fw.write(actualMark1[j]+","+predictedMark1[j]+"\n");
				    fw.close();
				}
				catch(IOException ioe)
				{
				    System.err.println("IOException: " + ioe.getMessage());
				}
			}
//			-------------------------------------------------------------
			double[] finalActualMark1 = wr.getFinalActualMarks();
			double[] finalPredictedMark1 = wr.getFinalPredictedMarks();
			for(int j=0;j<finalActualMark1.length;j++){
				System.out.println(finalActualMark1[j] +"		"+finalPredictedMark1[j]);
			}
//
			System.out.println("Running LSA and LSATDIF");
			LSA lsa = new LSA(foldFiles,index,testingFold,finalTestingFold);
			double[] actualMark2 = lsa.getActualMarks();
			double[] predictedMark2 = lsa.getPredictedMarks();
			for(int j=0;j<actualMark2.length;j++){
				System.out.println(actualMark2[j] +"		"+predictedMark2[j]);
			}
			
			//------------------LSA TESTING-------------------------
			for(int j=0;j<actualMark2.length;j++){
				try
				{
				    String filename= "LSA.csv";
				    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
				    fw.write(actualMark2[j]+","+predictedMark2[j]+"\n");
				    fw.close();
				}
				catch(IOException ioe)
				{
				    System.err.println("IOException: " + ioe.getMessage());
				}
			}
//			---------------------------------------------------------------
			
			System.out.println("Following marks are for final testing fold\n\n");
			double[] finalActualMark2 = lsa.getFinalActualMarks();
			double[] finalPredictedMark2 = lsa.getFinalPredictedMarks();
			for(int j=0;j<finalActualMark2.length;j++){
				System.out.println(finalActualMark2[j] +"		"+finalPredictedMark2[j]);
			}
				
			double[][] learningMarks = new    double[predictedMark.length][3];
			
			for(int j=0;j<actualMark.length;j++){
				double[] values = new double[]{predictedMark[j],predictedMark1[j],predictedMark2[j]};
				learningMarks[j] = values;
			}
			
			double[][] finalLearningMarks = new double[finalPredictedMark.length][4];
			
			for(int j=0;j<finalActualMark.length;j++){
				double[] values = new double[]{finalPredictedMark[j],finalPredictedMark1[j],finalPredictedMark2[j]};
				finalLearningMarks[j] = values;
			}
			
			Mark_Regression mr = new Mark_Regression(foldFiles,index,testingFold,finalTestingFold,learningMarks,actualMark,finalLearningMarks,finalActualMark);
			System.out.println("Finding final result using "+finalTestingFold);
			
			double[] tmpActualMarks = mr.getFinalActualMarks();
			double[] tmpPredictedMarks = mr.getCalculatedMark();
			
			try
			{
			    String filename= "logFile.txt";
			    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
			    for(int j=0;j<tmpActualMarks.length;j++){
			    	fw.write(tmpActualMarks[j]+","+tmpPredictedMarks[j]+"\n");
			    }
			    
			   // fw.write("Correlation Factor equals to "+new PearsonsCorrelation().correlation(tmpActualMarks, tmpPredictedMarks)+"\n");//appends the string to the file
			    fw.close();
			}
			catch(IOException ioe)
			{
			    System.err.println("IOException: " + ioe.getMessage());
			}
			
			
			for(int j=0;j<tmpActualMarks.length;j++){
				finalActualMarks[counter] = tmpActualMarks[j];
				finalPredictedMarks[counter] = tmpPredictedMarks[j];
				counter++;
			}
		}
		
		for(int i=0;i<finalActualMarks.length;i++){
			System.out.println("[DEBUG] "+finalActualMarks[i]+"		"+finalPredictedMarks[i]);
		}
		
		System.out.println("[DEBUG] The final correlation of the system is "+new PearsonsCorrelation().correlation(finalActualMarks,finalPredictedMarks));
		
		scatter sp = new scatter(("Actual Mark v/s Predicted Mark"), "Actual Marks", "Predicted Marks", finalActualMarks, finalPredictedMarks);
		sp.pack();
		sp.setVisible(true);
			
	}
	
	 public static int getSize(int[] index){
			int totalSize =0;
			for(int i=0;i<index.length;i++){
				
				totalSize += index[i];
				
			}
			return totalSize;
		}
}

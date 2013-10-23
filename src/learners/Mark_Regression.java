package learners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class Mark_Regression {
	File[][] foldFiles;
	int [] index;
	int testingFold;
	int finalTestingFold;
	double[][] learningMarks;
	double[] actualMarks;
	double[][] finalLearningMarks;
	double[] finalActualMarks;
	
	double[] calculatedMark;
	
	public double[] getFinalActualMarks() {
		return finalActualMarks;
	}
	
	public double[] getCalculatedMark() {
		return calculatedMark;
	}

	
	public Mark_Regression(File[][] fold,int[] fileIndex, int testing,int finalTesting,double[][] lm,double[]am,double[][]flm,double[]fam){
		foldFiles = fold;
		index = fileIndex;
		testingFold = testing;
		finalTestingFold = finalTesting;
		learningMarks = lm;
		actualMarks = am;
		finalLearningMarks = flm;
		finalActualMarks = fam;
		calculatedMark = new double[finalActualMarks.length];
		execute();
	}

	private void execute() {
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		regression.newSampleData(actualMarks, learningMarks);
		double [] coff = regression.estimateRegressionParameters();
		
			try
			{
			    String filename= "FinalEquation.txt";
			    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
			    fw.write("finalMark = "+coff[0]+" + ");
			    for(int i=1;i<coff.length;i++){
			    	if(i==coff.length){
			    		fw.write(coff[i]+"\n");
			    	}else{
			    		fw.write(coff[i]+" + ");
			    	}
			    }
			    fw.close();
			}
			catch(IOException ioe)
			{
			    System.err.println("IOException: " + ioe.getMessage());
			}
		
		
		for(int i=0;i<finalActualMarks.length;i++){
			double predicted = coff[0] + coff[1]*finalLearningMarks[i][0] + coff[2]*finalLearningMarks[i][1] + coff[3]*finalLearningMarks[i][2];
			calculatedMark[i] = predicted;
			System.out.println(finalActualMarks[i]+"	"+predicted);
		}
		
		System.out.println("Final correlation of the system is "+new PearsonsCorrelation().correlation( finalActualMarks ,calculatedMark));
	}
}

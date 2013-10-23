package learners;

import java.io.File;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import utils.Extract_Features;

public class Summary_Regression {
	File[][] foldFiles;
	int [] index;
	int testingFold;
	int finalTestingFold;
	double[] actualMarks;
	double[]predictedMarks;
	double[] finalActualMarks;
	double[] finalPredictedMarks;
	double [] coff;
	
	public Summary_Regression(File[][]fold,int [] fileIndex,int testing,int finalTesting){
		index = fileIndex;
		foldFiles = fold;
		testingFold = testing;
		finalTestingFold = finalTesting;
		actualMarks = new double[index[testingFold]];
		predictedMarks = new double[index[testingFold]];
		finalActualMarks = new double[index[finalTestingFold]];
		finalPredictedMarks = new double[index[finalTestingFold]];
		execute();
	}
	
	

	private void execute() {
		Extract_Features esp = new Extract_Features();
		double [] marksDataSet = new double[getSize(index,testingFold,finalTestingFold)+1];
	    double [][] parameterData = new double[getSize(index,testingFold,finalTestingFold)+1][];
		
		int counter =0;
		for(int i=0;i<foldFiles.length;i++){
			for(int j=0;j<index[i];j++){
				System.out.println("[Learning] "+ foldFiles[i][j].getName());
				if(j!=testingFold || j!=finalTestingFold){
					double[] parameters = new double[6];
					File tmpFile = foldFiles[i][j];
					parameters[0] = esp.totalImageCount(tmpFile);
					parameters[1] = esp.totalTableCount(tmpFile);
					parameters[2] = esp.totalWordCount(tmpFile);
					parameters[3] = esp.submissionTimeLeft(tmpFile);
					parameters[4] = esp.averageWordLength(tmpFile);
					parameters[5] = esp.wordsOverAverage(tmpFile,parameters[4]);
					String tmp[] = tmpFile.getName().split("_");
					marksDataSet[counter] = Integer.parseInt(tmp[1].substring(0,2));
					parameterData[counter] = parameters;
					counter++;
				}
			}
		}
		
		marksDataSet[counter] = 0;
		double [] tempArray = new double[6];
		for(int l=0;l<tempArray.length;l++){
			tempArray[l] =0;
		}
		parameterData[counter] = tempArray;
		
		System.out.println("Calculating regression coeff for testing set "+ testingFold);
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		regression.newSampleData(marksDataSet, parameterData);
		coff = regression.estimateRegressionParameters();
		
		int counter1 =0;
		int counter2 =0;
		for(int i=0;i<foldFiles.length;i++){
			for(int j=0;j<index[i];j++){
				if(i==testingFold || i == finalTestingFold){
					File tmpFile = foldFiles[i][j];
					String tmp[] = tmpFile.getName().split("_");
					
					if(i==testingFold){
					actualMarks[counter1] = Integer.parseInt(tmp[1].substring(0,2));
					
					double[] predictingValues = new double[6];
					predictingValues[0] = esp.totalImageCount(tmpFile);
					predictingValues[1] = esp.totalTableCount(tmpFile);
					predictingValues[2] = esp.totalWordCount(tmpFile);
					predictingValues[3] = esp.submissionTimeLeft(tmpFile);
					predictingValues[4] = esp.averageWordLength(tmpFile);
					predictingValues[5] = esp.wordsOverAverage(tmpFile,predictingValues[4]);
					
					predictedMarks[counter1] = coff[0];
					System.out.println(coff[0]+" + ");
					for(int l=0;l<predictingValues.length;l++){
						if(l==predictingValues.length-1){
						System.out.println(coff[l+1]*predictingValues[l]);
						}else{
						System.out.print(coff[l+1]*predictingValues[l]+" + ");
						}
						predictedMarks[counter1] += coff[l+1]*predictingValues[l];
					}
					
					if(predictedMarks[counter1]>100){
						predictedMarks[counter1] =100;
					}
					System.out.println(actualMarks[counter1]+"	"+predictedMarks[counter1]);
					counter1++;
					}
					
					if(i==finalTestingFold){
						finalActualMarks[counter2] = Integer.parseInt(tmp[1].substring(0,2));
						
						double[] predictingValues = new double[6];
						predictingValues[0] = esp.totalImageCount(tmpFile);
						predictingValues[1] = esp.totalTableCount(tmpFile);
						predictingValues[2] = esp.totalWordCount(tmpFile);
						predictingValues[3] = esp.submissionTimeLeft(tmpFile);
						predictingValues[4] = esp.averageWordLength(tmpFile);
						predictingValues[5] = esp.wordsOverAverage(tmpFile,predictingValues[4]);
						
						finalPredictedMarks[counter2] = coff[0];
						System.out.println(coff[0]+" + ");
						for(int l=0;l<predictingValues.length;l++){
							if(l==predictingValues.length-1){
							System.out.println(coff[l+1]*predictingValues[l]);
							}else{
							System.out.print(coff[l+1]*predictingValues[l]+" + ");
							}
							finalPredictedMarks[counter2] += coff[l+1]*predictingValues[l];
						}
						
						if(finalPredictedMarks[counter2]>100){
							finalPredictedMarks[counter2] =100;
						}
						System.out.println(finalActualMarks[counter2]+"	"+finalPredictedMarks[counter2]);
						counter2++;
					}
				}
			}
		}
	}

	public double[] getCoff() {
		return coff;
	}



	public int getSize(int[] index,int testingFold,int finalTestingFold){
		int totalSize =0;
		for(int i=0;i<index.length;i++){
			if(i!=testingFold || i!=finalTestingFold){
			totalSize += index[i];
			}
		}
		return totalSize;
	}

	public double[] getActualMarks() {
		return actualMarks;
	}

	public double[] getPredictedMarks() {
		return predictedMarks;
	}

	public double[] getFinalActualMarks() {
		return finalActualMarks;
	}

	public double[] getFinalPredictedMarks() {
		return finalPredictedMarks;
	}
	
}

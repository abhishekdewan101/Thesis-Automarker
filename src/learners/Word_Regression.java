package learners;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import utils.Extract_Features;

public class Word_Regression {
	File[][] foldFiles;
	int [] index;
	int testingFold;
	int finalTestingFold;
	double[] actualMarks;
	double[] predictedMarks;
	double[] finalActualMarks;
	double[] finalPredictedMarks;
	
	public Word_Regression(File[][]fold,int [] fileIndex,int testing,int finalTesting){
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
		ArrayList<String> bestWords = esp.trainingWords(foldFiles, index, testingFold, finalTestingFold);
		for(int i=0;i<foldFiles.length;i++){
			for(int j=0;j<index[i];j++){
				System.out.println("[Learning] "+ foldFiles[i][j].getName());
				if(j!=testingFold || j!=finalTestingFold){
					File tmpFile = foldFiles[i][j];
					double[] parameters = new double[bestWords.size()];
					HashMap<String,Integer> tmpHash = new HashMap<String,Integer>();
					try{
						FileChannel fileChannel;
						fileChannel = new FileInputStream(tmpFile).getChannel();
						ByteBuffer contentsBuffer = ByteBuffer.allocate((int)fileChannel.size()); 
					    fileChannel.read(contentsBuffer);
						fileChannel.close();
						String contents = new String(contentsBuffer.array());
						StringTokenizer st = new StringTokenizer(contents);
						
						while(st.hasMoreTokens()){
							String tempString = st.nextToken().toLowerCase();
							if(bestWords.contains(tempString)){
								if(tmpHash.containsKey(tempString)){
									tmpHash.put(tempString, tmpHash.get(tempString)+1);
								}else{
									tmpHash.put(tempString, 1);
								}
							}
						}
					}catch(IOException e){
						e.printStackTrace();
					}
					
					for(int l=0;l<bestWords.size();l++){
							int wordCount =0;
							if(tmpHash.containsKey(bestWords.get(l).toString())){
								wordCount = tmpHash.get(bestWords.get(l).toString());
							}else{
								wordCount = 0;
							}
							parameters[l] = wordCount;
						}
					
					String tmp[] = tmpFile.getName().split("_");
					marksDataSet[counter] = Integer.parseInt(tmp[1].substring(0,2));
					parameterData[counter] = parameters;
					counter++;
				}
			}
		}
		
		marksDataSet[counter] = 0;
		double [] tempArray = new double[bestWords.size()];
		for(int l=0;l<tempArray.length;l++){
			tempArray[l] =0;
		}
		parameterData[counter] = tempArray;
		
		System.out.println("Calculating regression coeff for testing set "+ testingFold);
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		regression.newSampleData(marksDataSet, parameterData);
		double [] coff = regression.estimateRegressionParameters();
		
		int counter1 =0;
		int counter2 =0;
		for(int i=0;i<foldFiles.length;i++){
			for(int j=0;j<index[i];j++){
				if(i==testingFold){
					File tmpFile = foldFiles[i][j];
					String tmp[] = tmpFile.getName().split("_");
					actualMarks[counter1] = Integer.parseInt(tmp[1].substring(0,2));
					
					double[] predictingValues = new double[bestWords.size()];
					
					HashMap<String,Integer> tmpHash1 = new HashMap<String,Integer>();	
					try{
							FileChannel fileChannel;
							fileChannel = new FileInputStream(tmpFile).getChannel();
							ByteBuffer contentsBuffer = ByteBuffer.allocate((int)fileChannel.size()); 
						    fileChannel.read(contentsBuffer);
							fileChannel.close();
							String contents = new String(contentsBuffer.array());
							StringTokenizer st = new StringTokenizer(contents);
							while(st.hasMoreTokens()){
								String tempString = st.nextToken();
								if(bestWords.contains(tempString)){
									if(tmpHash1.containsKey(tempString)){
										tmpHash1.put(tempString, tmpHash1.get(tempString)+1);
									}else{
										tmpHash1.put(tempString, 1);
									}
								}
							}
							System.out.println(tmpHash1);
							for(int l=0;l<bestWords.size();l++){
								if(tmpHash1.containsKey(bestWords.get(l).toString())){
								predictingValues[l] = tmpHash1.get(bestWords.get(l).toString());
								}else{
									predictingValues[l] =0;
								}
							}
							
							
						}catch(IOException e){
							e.printStackTrace();
						}
					
					
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
					File tmpFile = foldFiles[i][j];
					String tmp[] = tmpFile.getName().split("_");
					finalActualMarks[counter2] = Integer.parseInt(tmp[1].substring(0,2));
					
					double[] predictingValues = new double[bestWords.size()];
					
					HashMap<String,Integer> tmpHash1 = new HashMap<String,Integer>();	
					try{
							FileChannel fileChannel;
							fileChannel = new FileInputStream(tmpFile).getChannel();
							ByteBuffer contentsBuffer = ByteBuffer.allocate((int)fileChannel.size()); 
						    fileChannel.read(contentsBuffer);
							fileChannel.close();
							String contents = new String(contentsBuffer.array());
							StringTokenizer st = new StringTokenizer(contents);
							while(st.hasMoreTokens()){
								String tempString = st.nextToken();
								if(bestWords.contains(tempString)){
									if(tmpHash1.containsKey(tempString)){
										tmpHash1.put(tempString, tmpHash1.get(tempString)+1);
									}else{
										tmpHash1.put(tempString, 1);
									}
								}
							}
							System.out.println(tmpHash1);
							for(int l=0;l<bestWords.size();l++){
								if(tmpHash1.containsKey(bestWords.get(l).toString())){
								predictingValues[l] = tmpHash1.get(bestWords.get(l).toString());
								}else{
									predictingValues[l] =0;
								}
							}
							
							
						}catch(IOException e){
							e.printStackTrace();
						}
					
					
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

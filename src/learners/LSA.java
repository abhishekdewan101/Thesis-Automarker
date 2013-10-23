package learners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import Jama.Matrix;
import Jama.SingularValueDecomposition;


public class LSA {
	File[][] foldFiles;
	int [] index;
	int testingFold;
	int finalTestingFold;
	double[] actualMarks;
	double[] predictedMarks;
	double[] finalActualMarks;
	double[] finalPredictedMarks;
	Matrix U;
	Matrix S;
	Matrix vTranspose;
	Matrix U1;
	Matrix S1;
	Matrix vTranspose1;
	ArrayList<String> stopWords = new ArrayList<String>();
	ArrayList<String> dictionaryWords = new ArrayList<String>();
	ArrayList<String> refinedWords;
	ArrayList<String> documentsList = new ArrayList<String>();
	final int NUM_FACTORS = 1000;
	HashMap<Double,Double> finalSet = new HashMap<Double,Double>();

	
	public LSA(File[][] folds,int[] fileIndex,int testing,int finalTesting){
		foldFiles = folds;
		index = fileIndex;
		testingFold = testing;
		finalTestingFold = finalTesting;
		actualMarks = new double[index[testingFold]];
		predictedMarks = new double[index[testingFold]];
		
		finalActualMarks = new double[index[finalTestingFold]];
		finalPredictedMarks = new double[index[finalTestingFold]];
		
		File[] trainingFiles = new File[getSize(index,testingFold,finalTestingFold)];
		int count =0;
		for(int i=0;i<foldFiles.length;i++){
			for(int j=0;j<index[i];j++){
				if(i!= testingFold || i!=finalTestingFold){
				trainingFiles[count] = foldFiles[i][j];
				count++;
				}
			}
		}
		getDictionaryWords();
		calculateVectors(trainingFiles);
		testQuery();
	}
	
	private void testQuery(){
		int counter =0;
		int counter1 =0;
		for(int i=0;i<foldFiles.length;i++){
			for(int j=0;j<index[i];j++){
				if(i==testingFold || i==finalTestingFold){
				try{
					System.out.println("[LSA] querying "+ foldFiles[i][j].getName());
					FileInputStream fileInput = new FileInputStream(foldFiles[i][j]);
					FileChannel fileChannel = fileInput.getChannel();
					HashMap<String,Integer> words = new HashMap<String,Integer>();
					ByteBuffer contentsBuffer = ByteBuffer.allocate((int)fileChannel.size());
					fileChannel.read(contentsBuffer);
					fileChannel.close();
					String contents = new String(contentsBuffer.array());
					StringTokenizer st = new StringTokenizer(contents);
					while(st.hasMoreTokens()){
						String tempString = st.nextToken().toLowerCase();
						if(refinedWords.contains(tempString)){
							if(words.containsKey(tempString)){
								words.put(tempString, words.get(tempString)+1);
							}else{
								words.put(tempString, 1);
							}
						}
					}
					double[][] queryVector = new double[1][refinedWords.size()];

					for(int k=0;k<queryVector.length;k++)
					{
						queryVector[0][k] = 0.0;
					}


					for(String key:words.keySet()){
						int index = refinedWords.indexOf(key);
						queryVector[0][index] ++;
					}

					Matrix query = new Matrix(queryVector);
					
					Matrix sInverse = S.inverse();
					Matrix reducedQuery = query.times(U);
					reducedQuery = reducedQuery.times(sInverse);
					
					if(i==testingFold){
					double[][] queryArray =  reducedQuery.getArray();
					double[][] documents = vTranspose.getArray();
					double [] similarity = new double[documents.length];
					
					for(int k=0;k<documents.length;k++){
						double[] documentVector = documents[k];
						similarity[k] = calcSim(queryArray,documentVector);
					}
					
					System.out.println("Query v/s Documents\n for file "+foldFiles[i][j]);
					for(int k=0;k<similarity.length;k++){
						System.out.println(documentsList.get(k).toString() + "  has a similarity factor of "+ similarity[k]);
					}
					System.out.println("\n\n");

					HashMap<String,Double> bestResults = returnBestMatches(documentsList,similarity);

					double predicted =0.0;
					for(String key:bestResults.keySet()){
						String[] tmp = key.split("_");
						predicted += Integer.parseInt(tmp[1].substring(0,2));
					}
					
					predicted = predicted /50;
					String [] tmp = foldFiles[i][j].getName().split("_");
					double actualMark = Integer.parseInt(tmp[1].substring(0,2));
					System.out.println("Predicted mark is "+predicted +" and actual mark is "+actualMark);
					actualMarks[counter] = actualMark;
					predictedMarks[counter] = predicted;
					counter++;
					}
					
					//---------------------------------------------------------
					if(i==finalTestingFold){
						double[][] queryArray =  reducedQuery.getArray();
						double[][] documents = vTranspose.getArray();
						double [] similarity = new double[documents.length];
						
						for(int k=0;k<documents.length;k++){
							double[] documentVector = documents[k];
							similarity[k] = calcSim(queryArray,documentVector);
						}
						
						System.out.println("Query v/s Documents\n for file "+foldFiles[i][j]);
						for(int k=0;k<similarity.length;k++){
							System.out.println(documentsList.get(k).toString() + "  has a similarity factor of "+ similarity[k]);
						}
						System.out.println("\n\n");

						HashMap<String,Double> bestResults = returnBestMatches(documentsList,similarity);

						double predicted =0.0;
						for(String key:bestResults.keySet()){
							String[] tmp = key.split("_");
							predicted += Integer.parseInt(tmp[1].substring(0,2));
						}
						predicted = predicted /50;
						String [] tmp = foldFiles[i][j].getName().split("_");
						double actualMark = Integer.parseInt(tmp[1].substring(0,2));

					finalActualMarks[counter1] = actualMark;
					finalPredictedMarks[counter1] = predicted;
					
					counter1++;
					}
					
					}catch(IOException e){
					e.printStackTrace();
				}
			  }
			}
		}
	}
	private HashMap<String, Double> returnBestMatches(
			ArrayList<String> documentsList2, double[] similarity) {
		HashMap<String,Double> temp = new HashMap<String,Double>();
		while(temp.size()<50){
			double best =0.0;
			int indexOfBest =0;
			for(int i=0;i<similarity.length;i++){
				if(best<similarity[i] && !temp.containsKey(documentsList2.get(i).toString())){
					best = similarity[i];
					indexOfBest = i;
				}
			}
			System.out.println(documentsList2.get(indexOfBest).toString()+"	"+ indexOfBest);
			temp.put(documentsList2.get(indexOfBest).toString(), best);
		}
		return temp;
	}

	private void calculateVectors(File [] trainingFiles) {
		refinedWords = new ArrayList<String>();
		HashMap<String,ArrayList> words = new HashMap<String,ArrayList>();
		for(int i=0;i<trainingFiles.length;i++){
			System.out.println("[LSA] initial "+trainingFiles[i]);
			try {
				FileInputStream fileInput = new FileInputStream(trainingFiles[i]);
				FileChannel fileChannel = fileInput.getChannel();
				ByteBuffer contentsBuffer = ByteBuffer.allocate((int)fileChannel.size());
				fileChannel.read(contentsBuffer);
				fileChannel.close();
				String contents = new String(contentsBuffer.array());
				StringTokenizer st = new StringTokenizer(contents);
				while(st.hasMoreTokens()){
				String tempString = st.nextToken().toLowerCase();

				if(!refinedWords.contains(tempString) && dictionaryWords.contains(tempString) && !stopWords.contains(tempString)){
						refinedWords.add(tempString);
				}

				if(dictionaryWords.contains(tempString) && !stopWords.contains(tempString)){
					if(words.containsKey(tempString)){
						if(!words.get(tempString).contains(trainingFiles[i])){
							ArrayList<File> tmp = words.get(tempString);
							tmp.add(trainingFiles[i]);
							words.put(tempString, tmp);
						}
					}else{
						ArrayList<File> tmp = new ArrayList<File>();
						tmp.add(trainingFiles[i]);
						words.put(tempString,tmp);
					}
				}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println(refinedWords.size());

		for(int i =0;i<refinedWords.size();i++){
			if(words.get(refinedWords.get(i).toString()).size()<trainingFiles.length/2){
				refinedWords.remove(refinedWords.get(i).toString());
			}
		}

		System.out.println(refinedWords.size());

		double[][] countMatrix = new double[refinedWords.size()][trainingFiles.length];
		for(int i=0;i<countMatrix.length;i++){
			for(int j=0;j<countMatrix[i].length;j++){
				countMatrix[i][j] = 0;
			}
		}

		for(int i=0;i<trainingFiles.length;i++){
			try{
		    System.out.println("[LSA] countmatrix "+trainingFiles[i]);
		    documentsList.add(trainingFiles[i].getName());
			FileInputStream fileInput = new FileInputStream(trainingFiles[i]);
			FileChannel fileChannel = fileInput.getChannel();
			ByteBuffer contentsBuffer = ByteBuffer.allocate((int)fileChannel.size());
			fileChannel.read(contentsBuffer);
			fileChannel.close();
			String contents = new String(contentsBuffer.array());
			StringTokenizer st = new StringTokenizer(contents);
			while(st.hasMoreTokens()){
				String tempString = st.nextToken().toLowerCase();
				if(refinedWords.contains(tempString)){
				 int index = refinedWords.indexOf(tempString);
				 countMatrix[index][i] += 1; 
				}
			}

			}catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
//		Matrix tdif = transform(new Matrix(countMatrix));
//		
//		int n = tdif.getColumnDimension();
//	    for (int j = 0; j < tdif.getColumnDimension(); j++) {
//	      for (int i = 0; i < tdif.getRowDimension(); i++) {
//	        double matrixElement = (tdif).get(i, j);
//	        if (matrixElement > 0.0D) {
//	          double dm = countDocsWithWord(
//	        		  tdif.getMatrix(i, i, 0, tdif.getColumnDimension() - 1));
//	          tdif.set(i, j, tdif.get(i,j) * (1 + Math.log(n) - Math.log(dm)));
//	        }
//	      }
//	    }
//	    // Phase 2: normalize the word scores for a single document
//	    for (int j = 0; j < tdif.getColumnDimension(); j++) {
//	      double sum = sum(tdif.getMatrix(0, tdif.getRowDimension() -1, j, j));
//	      for (int i = 0; i < tdif.getRowDimension(); i++) {
//	    	  tdif.set(i, j, (tdif.get(i, j) / sum));
//	      }
//	    }
		
		//SingularValueDecomposition s = tdif.svd();
	    
		
		System.out.println("[LSA]   STARTED");
		Matrix A = new Matrix(countMatrix);
		SingularValueDecomposition s = A.svd();

		 U = s.getU();
	     S = s.getS();
	     Matrix V = s.getV();
	    vTranspose = V.transpose();
	    System.out.println("[LSA]   ENDED");
			
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


	private double calcSim(double[][] queryArray, double[] documentVector) {
		double similarity =0.0;
		// calculate numerator
		double sum =0;
		for(int i=0;i<documentVector.length;i++){
			sum += documentVector[i] * queryArray[0][i];
		}

		//calculate denominator
		double normB =0;
		for(int i=0;i<documentVector.length;i++){
			normB += documentVector[i]*documentVector[i];
		}
		normB = Math.sqrt(normB);

		double normA =0;
		for(int i=0;i<queryArray[0].length;i++){
			normA += queryArray[0][i]*queryArray[0][i];
		}
		normA = Math.sqrt(normA);

		similarity = (sum)/(normA * normB);

		return similarity;
	}
	
	public Matrix transform(Matrix matrix) {
	    for (int j = 0; j < matrix.getColumnDimension(); j++) {
	      double sum = sum(matrix.getMatrix(
	        0, matrix.getRowDimension() -1, j, j));
	      for (int i = 0; i < matrix.getRowDimension(); i++) {
	        matrix.set(i, j, (matrix.get(i, j) / sum));
	      }
	    }
	    return matrix;
	  }

	  private double sum(Matrix colMatrix) {
	    double sum = 0.0D;
	    for (int i = 0; i < colMatrix.getRowDimension(); i++) {
	      sum += colMatrix.get(i, 0);
	    }
	    return sum;
	  }

	 
		  private double countDocsWithWord(Matrix matrix) {
		    double numDocs = 0.0D;
		    for (int j = 0; j < matrix.getColumnDimension(); j++) {
		      if (matrix.get(0, j) > 0.0D) {
		        numDocs++;
		      }
		    }
		    return numDocs;
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
	  
	public void getDictionaryWords(){
		System.out.println("Updating local lists....");
		String[] fileName = {"brit-a-z.txt","wordList","britcaps.txt","csWords","stopWordList"};
		try {
			for(int i =0;i<fileName.length;i++){
				System.out.println("[LSA]  Setting up "+fileName[i]);
				File tmpFile = new File(fileName[i]);
			    FileChannel fileChannel = new FileInputStream(tmpFile).getChannel();
			    ByteBuffer contentsBuffer = ByteBuffer.allocate((int)fileChannel.size()); 
			    fileChannel.read(contentsBuffer);
				fileChannel.close();
				String contents = new String(contentsBuffer.array());
				StringTokenizer st = new StringTokenizer(contents);
				while(st.hasMoreTokens()){
					String tmp = st.nextToken().toLowerCase();
					if(fileName[i].equals("stopWordList")){
						stopWords.add(tmp);
					}else{
						if(!dictionaryWords.contains(tmp)){
							dictionaryWords.add(tmp);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

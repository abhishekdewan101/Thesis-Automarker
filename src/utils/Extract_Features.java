package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class Extract_Features {
		ArrayList<String> stopWords;
		ArrayList<String> dictionaryWords;
		
		//--------CONSTRUCTOR------------------
		public Extract_Features(){
			stopWords = new ArrayList<String>();
			dictionaryWords = new ArrayList<String>();
			System.out.println("[EXTRACT FEATURES]		"+"setting up word lists");
			String [] defaultFiles = {"brit-a-z.txt","wordList","britcaps.txt","csWords","stopWordList"};
			try{
				for(int i=0;i<defaultFiles.length;i++){
					System.out.println("Setting up "+defaultFiles[i]);
					File tmpFile = new File(defaultFiles[i]);
				    FileChannel fileChannel = new FileInputStream(tmpFile).getChannel();
				    ByteBuffer contentsBuffer = ByteBuffer.allocate((int)fileChannel.size()); 
				    fileChannel.read(contentsBuffer);
					fileChannel.close();
					String contents = new String(contentsBuffer.array());
					StringTokenizer st = new StringTokenizer(contents);
					while(st.hasMoreTokens()){
						String tmp = st.nextToken();
						if(defaultFiles[i].equals("stopWordList")){
							stopWords.add(tmp);
						}else{
							if(!dictionaryWords.contains(tmp)){
								dictionaryWords.add(tmp);
							}
						}
					}
				}
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		//---------METHODS---------------------
		
		public int totalWordCount(File inputFile){
			System.out.println("Extracting total word count for "+ inputFile.getName());
			
			int totalWordCount = 0;
			try{
			FileChannel fileChannel = new FileInputStream(inputFile).getChannel();
		    ByteBuffer contentsBuffer = ByteBuffer.allocate((int)fileChannel.size()); 
		    fileChannel.read(contentsBuffer);
			fileChannel.close();
			String contents = new String(contentsBuffer.array());
			StringTokenizer st = new StringTokenizer(contents);
			while(st.hasMoreTokens()){
				st.nextToken();
				totalWordCount++;
			}
			}catch(IOException e){
				e.printStackTrace();
			}
			return totalWordCount;
		}
		
		public int totalImageCount(File inputFile){
			//System.out.println("Extracting total image count for "+ inputFile.getName());
			String regex="(?i)(Figure [0-9]*[?.][?.0-9]* |Figure\\s[0-9]*[?-][0-9]*|Figure [0-9]*|Figure[0-9]*|Fig. [0-9][a-zA-Z]|Image [0-9]|Diagram [0-9])";
			Pattern stringChecker = Pattern.compile(regex);
			Matcher match;
			ArrayList<String> images = new ArrayList<String>();
			try{
				FileChannel fileChannel = new FileInputStream(inputFile).getChannel();
			    ByteBuffer contentsBuffer = ByteBuffer.allocate((int)fileChannel.size()); 
			    fileChannel.read(contentsBuffer);
				fileChannel.close();
				String contents = new String(contentsBuffer.array());
				match = stringChecker.matcher(contents);
				while(match.find()){
					if(match.group().length()!=0){
						
						if(images.contains(match.group())==false){
						images.add(match.group());
						}
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			return images.size();
			
		}
		
		public int totalTableCount(File inputFile){
			//System.out.println("Extracting total table count for "+ inputFile.getName());
			String regex="\\bTable [0-9]*-[0-9]*\\b|\\bTable ?[a-zA-Z][0-9]*?\\.|\\bTable [0-9]*?\\.[0-9]*?\\.?[0-9]*\\b|\\bTable [0-9]*\\b";
			Pattern stringChecker = Pattern.compile(regex);
			Matcher match;
			ArrayList<String> foundTables = new ArrayList<String>();
			try{
				FileChannel fileChannel = new FileInputStream(inputFile).getChannel();
			    ByteBuffer contentsBuffer = ByteBuffer.allocate((int)fileChannel.size()); 
			    fileChannel.read(contentsBuffer);
				fileChannel.close();
				String contents = new String(contentsBuffer.array());
				match = stringChecker.matcher(contents);
				while(match.find()){
					if(match.group().length()!=0){
						
						if(foundTables.contains(match.group())==false){
						foundTables.add(match.group());
						}
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			return foundTables.size();
		}
		
		public double averageWordLength(File inputFile){
		//	System.out.println("Finding average word length "+ inputFile.getName());
			int wordLength = 0;
			int wordCount = 0;
			try{
				FileChannel fileChannel = new FileInputStream(inputFile).getChannel();
			    ByteBuffer contentsBuffer = ByteBuffer.allocate((int)fileChannel.size()); 
			    fileChannel.read(contentsBuffer);
				fileChannel.close();
				String contents = new String(contentsBuffer.array());
				StringTokenizer st = new StringTokenizer(contents);
				while(st.hasMoreTokens()){
					String tempString = st.nextToken();
					if(dictionaryWords.contains(tempString)&&!stopWords.contains(tempString)){
						wordLength += tempString.length();
						wordCount++;
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			return (double) wordLength/wordCount;
		}
		
		public int wordsOverAverage(File inputFile,double averageLength){
			//System.out.println("Finding average word length "+ inputFile.getName());
			int wordCount = 0;
			try{
				FileChannel fileChannel = new FileInputStream(inputFile).getChannel();
			    ByteBuffer contentsBuffer = ByteBuffer.allocate((int)fileChannel.size()); 
			    fileChannel.read(contentsBuffer);
				fileChannel.close();
				String contents = new String(contentsBuffer.array());
				StringTokenizer st = new StringTokenizer(contents);
				while(st.hasMoreTokens()){
					String tempString = st.nextToken();
					if(dictionaryWords.contains(tempString)&&!stopWords.contains(tempString)){
						if(tempString.length()>=averageLength){
						wordCount++;
						}
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			return wordCount;
		}
		
		public double submissionTimeLeft(File inputFile){
		//	System.out.println("Extracting submission time left for "+ inputFile.getName());
			double submissionTimeLeft = 0;
			try {
				Connection databaseConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root","");  //change the dependency on SQL to serializable file later
				ResultSet resultSet;
				Statement databaseStatement = databaseConnection.createStatement();
				String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
				Date deadLine = new Date(2012,9,30,23,59,59);
				Date submittedDate;
				
				
				String tmp[] = inputFile.getName().split("_");
				resultSet = databaseStatement.executeQuery("select * from submissionTime where filename='"+tmp[0]+"'");
				while(resultSet.next()){
				    int day = Integer.parseInt(resultSet.getString(2).substring(0,2));
					int month = 0;
				    for(int i =0;i<months.length;i++){
					 if(months[i].contains(resultSet.getString(2).substring(3,6))){
						 month = i+1;
					 }
					}
				    
					int year = Integer.parseInt(resultSet.getString(2).substring(7,12).trim());
					int hrs = Integer.parseInt(resultSet.getString(3).substring(0,2));
					int mins = Integer.parseInt(resultSet.getString(3).substring(3,5));
					int seconds = Integer.parseInt(resultSet.getString(3).substring(6,8));
					
					submittedDate = new Date(year,month,day,hrs,mins,seconds);
					submissionTimeLeft = (deadLine.getTime() - submittedDate.getTime())/(1000*60); // value in minutes
				}
	 		} catch (SQLException e) {
				e.printStackTrace();
			}
			return submissionTimeLeft;
		}
		
		
		public ArrayList<String> trainingWords(File[][] foldFiles,int[] index,int testingFold,int finalTestingFold){
			ArrayList<String> bestWords = new ArrayList<String>();
			try{
				HashMap<String,HashMap<String,Integer>> outerHash = new HashMap<String,HashMap<String,Integer>>();
				for(int i=0;i<foldFiles.length;i++){
					for(int j=0;j<index[i];j++){
						if(i!=testingFold||i!=finalTestingFold){
							System.out.println("[Word Regression] "+foldFiles[i][j]);
							FileChannel fileChannel;
							fileChannel = new FileInputStream(foldFiles[i][j]).getChannel();
							ByteBuffer contentsBuffer = ByteBuffer.allocate((int)fileChannel.size()); 
						    fileChannel.read(contentsBuffer);
							fileChannel.close();
							String contents = new String(contentsBuffer.array());
							StringTokenizer st = new StringTokenizer(contents);
							
							while(st.hasMoreTokens()){
								String tempString = st.nextToken().toLowerCase();
								if(tempString.length()>2){
									if(dictionaryWords.contains(tempString) && !stopWords.contains(tempString)){
										if(outerHash.containsKey(tempString)){
											HashMap<String,Integer> innerHash = outerHash.get(tempString);
											if(innerHash.containsKey(foldFiles[i][j].getName())){
												innerHash.put(foldFiles[i][j].getName(),innerHash.get(foldFiles[i][j].getName())+1);
												outerHash.put(tempString, innerHash);
											}else{
												innerHash.put(foldFiles[i][j].getName(),1);
												outerHash.put(tempString, innerHash);
											}
										}else{
											HashMap<String,Integer> innerHash = new HashMap<String,Integer>();
											innerHash.put(foldFiles[i][j].getName(), 1);
											outerHash.put(tempString, innerHash);
										}
									}
								}
							}
						}
					}	
				}
				System.out.println(outerHash);
				
				HashMap<String,Double> correlationHash = new HashMap<String,Double>();
				
				int totalCount =0;
				for(int i =0;i<foldFiles.length;i++){
					for(int j=0;j<index[i];j++){
						totalCount++;
					}
				}
				
				int counter =0;
				for(String key:outerHash.keySet()){
					counter =0;
					HashMap<String,Integer> innerHash = outerHash.get(key);
					System.out.println(innerHash);
						if(innerHash.size()>60){
						double[] marks = new double[totalCount];
						double[] wordCount = new double[totalCount];
						for(int i=0;i<foldFiles.length;i++){
							for(int j=0;j<index[i];j++){
								if(i!= testingFold || i!= finalTestingFold){
								if(innerHash.containsKey(foldFiles[i][j].getName())){
										System.out.println(innerHash.size());
										String [] tmp = foldFiles[i][j].getName().split("_");
										marks[counter] = Integer.parseInt(tmp[1].substring(0,2));
										wordCount[counter] = innerHash.get(foldFiles[i][j].getName());
										counter++;
								}else{
									String [] tmp = foldFiles[i][j].getName().split("_");
									marks[counter] = Integer.parseInt(tmp[1].substring(0,2));
									wordCount[counter] = 0;
									counter++;
									}
								}
							}
						  }
						correlationHash.put(key,new PearsonsCorrelation().correlation(marks, wordCount));
					   }
					}
					
				
				while(bestWords.size()<=25){
					double bestValue = 0;
					String word = null;
					for(String key:correlationHash.keySet()){
						if(bestValue<correlationHash.get(key) && !bestWords.contains(key)){
							bestValue = correlationHash.get(key);
							word = key;
						}
					}
					try
					{
					    String filename= "bestWord.txt";
					    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
					    fw.write(word+" - "+correlationHash.get(word)+"\n");
					    fw.close();
					}
					catch(IOException ioe)
					{
					    System.err.println("IOException: " + ioe.getMessage());
					}
					bestWords.add(word);
				}
				
				System.out.println(bestWords);
			}catch(IOException e){
			e.printStackTrace();
			}
			return bestWords;
		}
}

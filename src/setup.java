import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;

public class setup {

	Connection databaseConnection = null;
	String 	   database;
	ResultSet  resultSet = null;
	String 	   databaseURL = null;
	String 	   baseURL = "jdbc:mysql://localhost:3306/";
	String	   userName = "root";
	String     password = "";
	
	
	//--------CONSTRUCTOR----------------------------------------
	public setup(String databaseName){
		databaseURL = baseURL+databaseName;
		database = databaseName;
		try {
			databaseConnection = DriverManager.getConnection(databaseURL,userName,password);
		} catch (SQLException e) {
			try {
				databaseConnection = DriverManager.getConnection(baseURL,userName,password); // if the database name given is not present then log into any database and run the create database query.
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		create_database();
		create_tables();
		populate_tables();
	}
	
	
	//----------METHODS---------------------------------------
	public void create_database(){
		System.out.println("Creating database "+database+".....");
		try {
			Statement databaseStatement = databaseConnection.createStatement();
			databaseStatement.executeUpdate("Create database "+database+";");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Created database "+database+".....");
	}
	
	
	public void create_tables(){
		System.out.println("Populating database with tables. Please wait.....");
		try {
			Statement databaseStatement = databaseConnection.createStatement();
			databaseStatement.executeUpdate("use "+database);
			
			//databaseStatement.executeUpdate("CREATE TABLE `imageDB` (`noOfImages` int(11) NOT NULL,`marks` int(11) NOT NULL,`filename` varchar(255) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			
			//databaseStatement.executeUpdate("CREATE TABLE `stopWordDB` (`word` varchar(255) NOT NULL DEFAULT '',UNIQUE KEY `word` (`word`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			
			databaseStatement.executeUpdate("CREATE TABLE `submissionTime` (`filename` varchar(255) NOT NULL,`date` varchar(255) NOT NULL DEFAULT '',`time` varchar(255) NOT NULL DEFAULT '',`marks` int(11) NOT NULL,`extendedDeadline` varchar(255) NOT NULL DEFAULT '') ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			
			//databaseStatement.executeUpdate("CREATE TABLE `wordDB` (`word` varchar(255) NOT NULL DEFAULT '',`correctGrammar` varchar(255) NOT NULL DEFAULT '',`wordCount` int(11) NOT NULL DEFAULT '0',`wordLength` int(11) NOT NULL DEFAULT '0',`marks` int(11) NOT NULL DEFAULT '0',`filename` varchar(255) NOT NULL DEFAULT '') ENGINE=InnoDB DEFAULT CHARSET=latin1;");
		    
			//databaseStatement.executeUpdate("CREATE TABLE `wordList` (`word` varchar(255) NOT NULL DEFAULT '',UNIQUE KEY `word` (`word`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
		
			//databaseStatement.executeUpdate("CREATE TABLE `totalWords` (`wordCount` int(11) NOT NULL,`marks` int(11) NOT NULL,`filename` varchar(255) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			
			//databaseStatement.executeUpdate("CREATE TABLE `correlationDB` (`correlation` double NOT NULL,`word` varchar(255) NOT NULL DEFAULT '') ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			
			//databaseStatement.executeUpdate("CREATE TABLE `uniqueWords` (`word` varchar(255) NOT NULL DEFAULT '') ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			
			//databaseStatement.executeUpdate("CREATE TABLE `tableInfo` (`tableCount` int(11) NOT NULL,`filename` varchar(255) NOT NULL DEFAULT '' ENGINE=InnoDB DEFAULT CHARSET=latin1;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Done.......");
	}
	
	
	public void populate_tables(){
System.out.println("Extracting Submission time and populating the database");
		
		File submissionFile = new File("subtime.txt");
        File[] thesisFiles = new File("theses").listFiles();		
		String line;
		String date;
		String time;
		String id;
		String extended;
		int marks = 0;
		Statement databaseStatement = null;
		BufferedReader reader = null;
		try {
			 databaseStatement = databaseConnection.createStatement();
			 reader = new BufferedReader(new FileReader(submissionFile));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {			
			while((line = reader.readLine())!= null){
				id = line.substring(0, 4);
				date = line.substring(9,21);
				time = line.substring(21,29);
				
				//find the marks associated with the id
				for(int i =0;i<thesisFiles.length;i++){
					if(thesisFiles[i].getName().contains(id)){
						System.out.println("Processing "+thesisFiles[i].getName()+"....");
						String[] tmp = thesisFiles[i].getName().split("\\_");
						marks = Integer.parseInt(tmp[1].substring(0,2));
						break;
					}
				}
				
				//find whether there was a extended deadline
				if(line.substring(line.length()-1,line.length()).contains("E")){
					extended = "YES";
				}else{
					extended = "NO";
				}
				databaseStatement.executeUpdate("insert into submissionTime values('"+id+"','"+date+"','"+time+"','"+marks+"','"+extended+"')");
				
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		System.out.println("Done......");
	}
}

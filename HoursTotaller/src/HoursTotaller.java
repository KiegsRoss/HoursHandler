import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.geometry.*;
import javafx.scene.control.Button;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.image.Image;
import java.lang.Integer;
import java.io.*;
import java.lang.Float;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class HoursTotaller extends Application{
	
	private TextField name = new TextField();
	private TextField numFiles = new TextField();
	private TextField currYear = new TextField();
	private TextField currMonth = new TextField();
	private Button submitBtn = new Button("Submit");
	private int totalFiles = 0;
	private String employeeName = "";
	private TextField[] files;
	private int thisYear;
	private String thisMonth = "";
	private int nowMonth = 0;
	private List<WorkDay> sessions = new ArrayList<WorkDay>();
	
	@Override
	public void start(Stage primaryStage) {
		
		//first pane
		GridPane firstPane = new GridPane();
		firstPane.setAlignment(Pos.CENTER);
		firstPane.setPadding(new Insets(20));
		firstPane.setHgap(6);
		firstPane.setVgap(10);
		
		//nodes for first pane
		firstPane.add(new Label("Employee Name: "), 0, 0);
		firstPane.add(name, 1, 0);
		name.setText("");
		firstPane.add(new Label("Number of Files: "), 0, 1);
		firstPane.add(numFiles , 1, 1);
		numFiles.setText("");
		firstPane.add(new Label("Current Month: "), 0, 2);
		firstPane.add(currMonth, 1, 2);
		currMonth.setText("");
		firstPane.add(new Label("Current Year: "), 0, 3);
		firstPane.add(currYear, 1, 3);
		currYear.setText("");
		
		//add button and set up handler
		firstPane.add(submitBtn, 1, 4);
		GridPane.setHalignment(submitBtn, HPos.LEFT);
		submitBtn.setOnAction(e -> getFiles(primaryStage));
		currYear.setOnAction(e -> getFiles(primaryStage));
		
		//add fyi image to background
		
			
			InputStream image = ClassLoader.getSystemResourceAsStream("fyi.png");
			Image fyi = new Image(image);
			
			BackgroundImage myBI= new BackgroundImage(fyi,
			        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
			          BackgroundSize.DEFAULT);
			firstPane.setBackground(new Background(myBI));
		
		
		
		
		//setting and showing the firstPane
		Scene scene = new Scene(firstPane);
		primaryStage.setTitle("Hours Extractor");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	
	
	
	private void getFiles(Stage primaryStage){
		
		
	    //get name and number of files to parse to show appropriate number of 
		//text fields
		employeeName = name.getText();
		totalFiles = Integer.parseInt(numFiles.getText());
		files = new TextField[totalFiles];
		thisYear = Integer.parseInt(currYear.getText());
		thisMonth = currMonth.getText();
		
		try {
			nowMonth = getMonthNum(thisMonth);
			if(nowMonth == 0) {
				throw new NumberFormatException();
			}
		}catch(NumberFormatException e) {
			
			//set up pane for the error message
			GridPane exceptionPane = new GridPane();
			exceptionPane.setAlignment(Pos.CENTER);
			exceptionPane.setPadding(new Insets(20));
			exceptionPane.setHgap(6);
			exceptionPane.setVgap(10);
			
			Label error = new Label(thisMonth + " is not an actual month.");
			exceptionPane.add(error, 0, 0);
			GridPane.setHalignment(error, HPos.CENTER);
			
			Scene scene = new Scene(exceptionPane);
			primaryStage.setTitle("Hours Extractor");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			//set delay so user can read error message
			try {
				TimeUnit.SECONDS.sleep(5);
			}catch(InterruptedException ie) {
				
			}
			
			//return to getting the files from the user
			start(primaryStage);
			return;
			
		}
		
		//create and pad the second pane
		GridPane secondPane = new GridPane();
		secondPane.setAlignment(Pos.CENTER);
		secondPane.setPadding(new Insets(20));
		secondPane.setHgap(6);
		secondPane.setVgap(10);
		
		//populate text fields based on the number of files given
		for(int i = 0; i < totalFiles; i++) {
			files[i] = new TextField();
			secondPane.add(new Label("File: "), 0, i);
			secondPane.add(files[i], 1, i);
		}
		
		//add button and set up handler
		Button addBtn = new Button("Extract Hours");
		secondPane.add(addBtn, 1, totalFiles);
		GridPane.setHalignment(addBtn, HPos.LEFT);
		addBtn.setOnAction(e -> addHours(primaryStage));
		files[totalFiles - 1].setOnAction(e -> addHours(primaryStage));
		
		//add fyi image to background
		InputStream image = ClassLoader.getSystemResourceAsStream("fyi.png");
		Image fyi = new Image(image);
		BackgroundImage myBI= new BackgroundImage(fyi,
		        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
		          BackgroundSize.DEFAULT);
		secondPane.setBackground(new Background(myBI));
		
		
		//set and display the second pane on stage
		Scene secondScene = new Scene(secondPane);
		primaryStage.setTitle("Hours Extractor");
		primaryStage.setScene(secondScene);
		primaryStage.show();
	}
	
	
	
	public void addHours(Stage primaryStage) {
		
		//get the list of files to parse through from previous text fields
		String[] fileList = new String[totalFiles];
		for(int i = 0; i < totalFiles; i++) {
			
			fileList[i] = files[i].getText();
			
		}
		
		float docHours = 0;
		float commHours = 0;
		float houseHours = 0;
		float travelHours = 0;
		float consultHours = 0;
		int hoursCounter = 0;
		String menteeName = "";
		int sessionDay = 0;
		byte curr;
		
		for(int i = 0; i < fileList.length; i++) {
			try{
				
				//open each file
				FileInputStream hours = new FileInputStream(fileList[i]);
				
				//set up docx to be read and get the paragraphs
				XWPFDocument xdoc = new XWPFDocument(hours);
				List<XWPFParagraph> paragraphs = xdoc.getParagraphs();
				
				for(XWPFParagraph para: paragraphs) {
					
					//get the text for each paragraph
					String text = para.getText();
					
					if(text.contains("Referral")) {
						
						byte[] textBytes = text.getBytes();
						
						for(int j = 0; j < textBytes.length; j++) {
							
							curr = textBytes[j];
							int offset = 2;
							int whitespaceCounter = 0;
							
							if(((char)curr == 'e' || (char)curr == 'l') && (char)textBytes[j + 1] == ':') {
								
								while((char)textBytes[j + offset] == ' ') {
									offset++;
								}
								
								do {
									
									menteeName = menteeName + (char)textBytes[j+offset];
									if((char)textBytes[j+offset] == ' ') whitespaceCounter++;
									offset++;
									
								}while( whitespaceCounter <= 1);
								break;
							}
						}
					}else if(text.contains(thisMonth) && text.contains(",")) {
						
						byte[] monthBytes = thisMonth.getBytes();
						byte[] textBytes = text.getBytes();
						
						for(int j = 0; j < textBytes.length; j++) {
							
							curr = textBytes[j];
							int offset = 2;
							
							if(((char)curr == (char)monthBytes[thisMonth.length() - 1]) && (49 <= textBytes[j + 2] && textBytes[j + 2] <= 57) ) {
								
								while((char)textBytes[j+offset] == ' ') {
									offset++;
								}
								
								String dayString = "";
								do {
									dayString = dayString + (char)textBytes[j + offset];
									offset++;
								}while(textBytes[j+offset] != ',');
								
								sessionDay = Integer.parseInt(dayString);
								
							}
							
						}
					}
					else if(text.contains("Consult")) { //checks for consult time to be added
						
						//get the bytes to parse through
						byte[] textBytes = text.getBytes();
						
						for(int j = 0; j < textBytes.length; j++) {
							
							curr = textBytes[j];
							int offset = 1;
							
							if((char)curr == '=') {
								
								//trim whitespace
								while((char)(curr = textBytes[j + offset]) == ' ') {
									offset++;
								}
								
								String addUp = "";
	
								//get the hours to add to running consult total
								// and converts it to a float
								do {
									
									addUp = addUp + (char)curr;
									offset++;
										
								}while((char)(curr = textBytes[j + offset]) != ' ' && ((char)curr != 'h'));
						
								consultHours += Float.parseFloat(addUp);
							}
						}
					//checks for the rest of the hours to be totaled	
					}else if(text.contains("=")) {
						
						//gets the bytes to be parsed through
						byte[] textBytes = text.getBytes();
						
						for(int j = 0; j < textBytes.length; j++) {
							
							curr = textBytes[j];
							int offset = 1;
							
							if((char)curr == '=') {
								
								//trim whitespace
								while((char)(curr = textBytes[j + offset]) == ' ') {
									offset++;
								}
								
								//create and string together hours number
								String addUp = "";
								do {
									
									addUp = addUp + (char)curr;
									offset++;
										
								}while((char)(curr = textBytes[j + offset]) != ' ' && ((char)curr != 'h'));
									
								//add the hours to the appropriate hours total
								try {
								
									if(hoursCounter == 0) {
										travelHours = Float.parseFloat(addUp);
										hoursCounter++;
									
									}else if(hoursCounter == 1) {
										houseHours = Float.parseFloat(addUp);
										hoursCounter++;
									
									}else if(hoursCounter == 2) {
										commHours = Float.parseFloat(addUp);
										hoursCounter++;
									
									}else if(hoursCounter == 3) {
										docHours = Float.parseFloat(addUp);
										hoursCounter = 0;
										
										WorkDay workDay = new WorkDay(travelHours
												, houseHours, commHours, docHours, nowMonth, sessionDay, 
												thisYear, menteeName, employeeName);
										sessions.add(workDay);
										
									}
								}catch(NumberFormatException e) {
									
									
									
								}
							}
							
						}
					
					}
				
				}
				xdoc.close();
				hours.close();
			
			}catch(FileNotFoundException e) {
				
				System.out.println("Could not find the file named '"+fileList[i]+"'");
				
				//set up pane for the error message
				GridPane thirdPane = new GridPane();
				thirdPane.setAlignment(Pos.CENTER);
				thirdPane.setPadding(new Insets(20));
				thirdPane.setHgap(6);
				thirdPane.setVgap(10);
				
				Label error = new Label("Could not find the file named '"+fileList[i]+"'");
				thirdPane.add(error, 0, 0);
				GridPane.setHalignment(error, HPos.CENTER);
				
				Scene scene = new Scene(thirdPane);
				primaryStage.setTitle("Hours Extractor");
				primaryStage.setScene(scene);
				primaryStage.show();
				
				//set delay so user can read error message
				try {
					TimeUnit.SECONDS.sleep(5);
				}catch(InterruptedException ie) {
					
				}
				
				//return to getting the files from the user
				getFiles(primaryStage);
				return;
			
			}catch(IOException e) {
				
				System.out.println("There was an IO exception :{ ");
				System.exit(0);
				
			}
			menteeName = "";
		}
		
		//set up pane for final window
		GridPane thirdPane = new GridPane();
		thirdPane.setAlignment(Pos.CENTER);
		thirdPane.setPadding(new Insets(20));
		thirdPane.setHgap(6);
		thirdPane.setVgap(10);
		
		//populate the text fields with the caclulated hours
		TextField travel = new TextField();
		travel.setText(Float.toString(travelHours));
		TextField house = new TextField();
		house.setText(Float.toString(houseHours));
		TextField community = new TextField();
		community.setText(Float.toString(commHours));
		TextField doc = new TextField();
		doc.setText(Float.toString(docHours));
		TextField consult = new TextField();
		consult.setText(Float.toString(consultHours));
		
		//set the labels and textfields for the calculated hours on the scene
		thirdPane.add(new Label("Ta Daaaaaaa"), 0, 0);
		
		for(WorkDay wd : sessions) {
			
			System.out.println(wd.getDate() + " "+wd.getMentee()+" : Travel = "+wd.getTravel()+" House = "+wd.getHouse());
			
		}
		
		//Create button and set up to restart on click
		Button runAgain = new Button("Add More Files");
		thirdPane.add(runAgain, 1, 5);
		GridPane.setHalignment(runAgain, HPos.RIGHT);
		runAgain.setOnAction(e -> start(primaryStage));
		
		//add fyi image to background
		InputStream image = ClassLoader.getSystemResourceAsStream("fyi.png");
		Image fyi = new Image(image);
		BackgroundImage myBI= new BackgroundImage(fyi,
		        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
		          BackgroundSize.DEFAULT);
		thirdPane.setBackground(new Background(myBI));
		
		
		Scene scene = new Scene(thirdPane);
		primaryStage.setTitle("Hours Extractor");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
		
		
	}
	
	
	
	
	public int getMonthNum(String month) {
		
		if(month.equals("January") || month.equals("january")) {
			return 1;
		}else if(month.equals("February") || month.equals("february")) {
			return 2;
		}else if(month.equals("March") || month.equals("march")) {
			return 3;
		}else if(month.equals("April") || month.equals("april")) {
			return 4;
		}else if(month.equals("May") || month.equals("may")) {
			return 5;
		}else if(month.equals("June")  || month.equals("june")) {
			return 6;
		}else if(month.equals("July") || month.equals("july")) {
			return 7;
		}else if(month.equals("August") || month.equals("august")) {
			return 8;
		}else if(month.equals("September") || month.equals("september")) {
			return 9;
		}else if(month.equals("October") || month.equals("october")) {
			return 10;
		}else if(month.equals("November") || month.equals("november")) {
			return 11;
		}else if(month.equals("December") || month.equals("december")) {
			return 12;
		}else {
			return 0;
		}
	}
	
	
	
	
	public static void main(String[] args) {
		
		Application.launch(args);

	}

}

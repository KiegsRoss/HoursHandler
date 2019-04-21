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
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.lang.Integer;
import java.io.*;
import java.lang.Float;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class HoursTotaller extends Application{
	
	private TextField name = new TextField();
	private TextField numFiles = new TextField();
	private TextField currYear = new TextField();
	private TextField currMonth = new TextField();
	private int totalFiles = 0;
	private String employeeName = "";
	private TextField[] files;
	private int thisYear;
	private String thisMonth = "";
	private int nowMonth = 0;
	private int id = 1;
	private List<WorkDay> sessions = new ArrayList<WorkDay>();
	private TextField excelFileField = new TextField();
	private static String[] headers = {"ID", "ClientName", "Assignment", "DTE", "TravelTime", 
	                                   "HouseTime", "CommunityTime", "Office/Docum", "ConsultTime"};
	
	/**
	 * This method creates the first window of the program that asks the user for the 
	 * employee name, number of files, and the month and year of the files that are being 
	 * read in.
	 * 
	 * @param primaryStage This is the stage that is used to show all of the scenes 
	 * throughout the program.
	 */
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
		Button submitBtn = new Button("Sumbit");
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
	
	/**
	 * This method takes in the employee name, number of files to be read, the month, and 
	 * the year from the previous scene. It then sets up the  next scene depending on how
	 * many files the user specified.
	 * 
	 * @param primaryStage This is the stage that is used to show all of the scenes
	 * throughout the program.
	 */
	private void getFiles(Stage primaryStage){
		
		
	    //get name and number of files to parse to show appropriate number of 
		//text fields
		int index = 0;
		employeeName = name.getText();
		totalFiles = Integer.parseInt(numFiles.getText());
		files = new TextField[totalFiles];
		List<IndexBtn> fileBtns = new ArrayList<IndexBtn>();
		for(int i = 0; i < totalFiles; i++) {
			fileBtns.add(i, new IndexBtn(new Button("Browse..."), i));
		}
		thisYear = Integer.parseInt(currYear.getText());
		thisMonth = currMonth.getText();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Word Files", "*.docx"));
		
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
			IndexBtn temp = fileBtns.get(i);
			secondPane.add(temp.getButton(), 2, i);
			temp.getButton().setOnAction(e -> setFileName(files, temp.getIndex(), fileChooser, primaryStage));
		}
		
		
		
		//add button and set up handler
		Button addBtn = new Button("Extract Hours");
		secondPane.add(addBtn, 1, totalFiles);
		GridPane.setHalignment(addBtn, HPos.LEFT);
		addBtn.setOnAction(e -> extractHours(primaryStage));
		files[totalFiles - 1].setOnAction(e -> extractHours(primaryStage));
		
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
	
	public void setFileName(TextField[] fields, int i, FileChooser fc, Stage primaryStage) {
		
		File file = fc.showOpenDialog(primaryStage);
		
		fields[i].setText(file.getPath());
		
	}
	
	/**
	 * This method opens the files specified by the user, and then reads in all of the 
	 * hours. It creates workday objects for session and adds them to the overall session 
	 * list.
	 * 
	 * @param primaryStage This is the stage that is being used to display all of the 
	 * scenes throughout the program.
	 */
	public void extractHours(Stage primaryStage) {
		
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
					
					
					//checks if the paragraph contains the referral's name and extracts it
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
					//checks if paragraph is the start of a session summary and extracts the date
					}else if(text.contains(thisMonth) && text.contains(",")) {
						
						byte[] monthBytes = thisMonth.getBytes();
						byte[] textBytes = text.getBytes();
						
						for(int j = 0; j < textBytes.length; j++) {
							
							curr = textBytes[j];
							int offset = 2;
							int whitespaceCounter = 0;
							
							if(((char)curr == (char)monthBytes[thisMonth.length() - 1]) && (49 <= textBytes[j + 2] && textBytes[j + 2] <= 57) ) {
								
								while((char)textBytes[j+offset] == ' ') {
									offset++;
								}
								
								String dayString = "";
								do {
									dayString = dayString + (char)textBytes[j + offset];
									offset++;
									
									if((char)textBytes[j+offset] == ' ') {
										whitespaceCounter++;
									}
									
								}while(textBytes[j+offset] != ',' && whitespaceCounter < 1);
								
								sessionDay = Integer.parseInt(dayString);
								break;
							}
						}
					//checks for hours to be read into a session	
					}else if(text.contains("=")) {
						if(text.contains("Consult")) { //checks for consult time to be read
							
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
	
									do {
										
										addUp = addUp + (char)curr;
										offset++;
											
									}while((char)(curr = textBytes[j + offset]) != ' ' && ((char)curr != 'h'));
							
									consultHours = Float.parseFloat(addUp);
									boolean alreadyThere = false;
									for(WorkDay session: sessions) {
										
										if(sessionDay == session.getDay() && menteeName == session.getMentee() && employeeName == session.getMentor()) {
											
											session.setConsult(consultHours);
											alreadyThere = true;
										}
										
									}
									
									if(!alreadyThere) {
										WorkDay session = new WorkDay(id++, 0, 0, 
												0, 0, consultHours, nowMonth, 
												sessionDay, thisYear, menteeName, 
												employeeName);
										sessions.add(session);
									}
								}
							}
						}else {	//checks for non-consult hours to be read
							
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
											
											
											boolean alreadyThere = false;
											for(WorkDay session: sessions) {
												
												if(sessionDay == session.getDay() && menteeName == session.getMentee() && employeeName == session.getMentor()) {
													
													session.setTravel(travelHours);
													session.setHouse(houseHours);
													session.setComm(commHours);
													session.setDoc(docHours);
													alreadyThere = true;
												}
											}
											
											if(!alreadyThere) {
												WorkDay workDay = new WorkDay(id++, travelHours
														, houseHours, commHours, docHours, (float)0,nowMonth, sessionDay, 
														thisYear, menteeName, employeeName);
												sessions.add(workDay);
											}
										}
									}catch(NumberFormatException e) {
										e.printStackTrace();	
									}
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
		
		//set up pane for window confirming hours were added
		GridPane thirdPane = new GridPane();
		thirdPane.setAlignment(Pos.CENTER);
		thirdPane.setPadding(new Insets(20));
		thirdPane.setHgap(6);
		thirdPane.setVgap(10);
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(3.0);
		dropShadow.setOffsetY(1.0);
		dropShadow.setSpread(0.75);
		dropShadow.setColor(Color.WHITE);
		
		//set the labels and textfields for the calculated hours on the scene
		Label confirm = new Label(employeeName+"'s session hours have been extracted.");
		confirm.setEffect(dropShadow);
		confirm.setMaxWidth(170);
		confirm.setWrapText(true);
		thirdPane.add(confirm, 0, 0);
		
		for(WorkDay wd : sessions) {
			
			System.out.println(wd.getDate() + " "+wd.getMentor()+" "+wd.getMentee()+
					" : Travel = "+wd.getTravel()+" House = "+wd.getHouse()+" Comm = "+
					wd.getComm()+" Doc = "+wd.getDoc()+" Consult = "+wd.getConsult());
			
		}
		
		//Create button and set up to restart on click
		Button runAgain = new Button("Add More Files");
		thirdPane.add(runAgain, 0, 1);
		GridPane.setHalignment(runAgain, HPos.CENTER);
		runAgain.setOnAction(e -> start(primaryStage));
		
		//Create button to make the xlsx file
		Button createFile = new Button("Create Excel File");
		thirdPane.add(createFile, 1, 1);
		GridPane.setHalignment(createFile, HPos.CENTER);
		createFile.setOnAction(e -> getFileName(primaryStage));
		
		//add fyi image to background
		InputStream image = ClassLoader.getSystemResourceAsStream("fyi.png");
		Image fyi = new Image(image);
		BackgroundImage myBI= new BackgroundImage(fyi,
		        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
		          BackgroundSize.DEFAULT);
		Background back = new Background(myBI);
		thirdPane.setBackground(back);
	
		
		
		Scene scene = new Scene(thirdPane);
		primaryStage.setTitle("Hours Extractor");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * This method gets the name of the files that will be written, and then it leads to 
	 * the method that actually writes the file.
	 * 
	 * @param primaryStage This is the stage that is being used to display all of the 
	 * scenes throughout the program.
	 */
	public void getFileName(Stage primaryStage) {
		
		//set up pane for scene that gets the filename from user
		GridPane namePane = new GridPane();
		namePane.setAlignment(Pos.CENTER);
		namePane.setPadding(new Insets(20));
		namePane.setHgap(6);
		namePane.setVgap(10);
		
		namePane.add(new Label("Enter the excel filename: "), 0, 0);
		namePane.add(excelFileField, 1, 0);
		
		Button writeFile = new Button("Write to File");
		namePane.add(writeFile, 1, 1);
		GridPane.setHalignment(writeFile, HPos.LEFT);
		writeFile.setOnAction(e -> createFile(primaryStage));
		excelFileField.setOnAction(e -> createFile(primaryStage));
		
		//add fyi image to background
		InputStream image = ClassLoader.getSystemResourceAsStream("fyi.png");
		Image fyi = new Image(image);
		BackgroundImage myBI= new BackgroundImage(fyi,
				BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				BackgroundSize.DEFAULT);
		namePane.setBackground(new Background(myBI));
		
		//create scene and show on stage
		Scene scene = new Scene(namePane);
		primaryStage.setTitle("Hours Extractor");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
	}
	
	/**
	 * This method creates a worksheet, creates a row for each session, and then fills
	 * the rows in with the data from each session. Once it sets all of this up, it 
	 * writes the worksheet to a file with the user-given file name.
	 * 
	 * @param primaryStage This is the stage that is being used to display all of the 
	 * scenes throughout the program.
	 */
	public void createFile(Stage primaryStage) {
		
		String fileName = excelFileField.getText();
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		
		int rowNum = 0;
		
		Row headerRow = sheet.createRow(rowNum++);
		int headerNum = 0;
		
		//creates all of the necessary cells for each header
		for(int i = 0; i < headers.length; i++) {
			
			Cell headerCell = headerRow.createCell(headerNum++);
			headerCell.setCellValue(headers[i]);
		}
		
		//create all of the necessary cells for each session row
		for(WorkDay session: sessions) {
			
			Row row = sheet.createRow(rowNum++);
			
			int colNum = 0;
			Cell idCell = row.createCell(colNum++);
			idCell.setCellValue(session.getID());
			Cell menteeCell = row.createCell(colNum++);
			menteeCell.setCellValue(session.getMentee());
			Cell mentorCell = row.createCell(colNum++);
			mentorCell.setCellValue(session.getMentor());
			Cell dateCell = row.createCell(colNum++);
			dateCell.setCellValue(session.getDate());
			Cell travelCell = row.createCell(colNum++);
			travelCell.setCellValue(session.getTravel());
			Cell houseCell = row.createCell(colNum++);
			houseCell.setCellValue(session.getHouse());
			Cell commCell = row.createCell(colNum++);
			commCell.setCellValue(session.getComm());
			Cell docCell = row.createCell(colNum++);
			docCell.setCellValue(session.getDoc());
			Cell consultCell = row.createCell(colNum++);
			consultCell.setCellValue(session.getConsult());
			
		}
		
		try {//write the workbook to the file with name given by user
            FileOutputStream outputStream = new FileOutputStream(fileName);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		GridPane finishedPane = new GridPane();
		finishedPane.setAlignment(Pos.CENTER);
		finishedPane.setPadding(new Insets(20));
		finishedPane.setHgap(6);
		finishedPane.setVgap(10);
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(3.0);
		dropShadow.setOffsetY(1.0);
		dropShadow.setSpread(0.75);
		dropShadow.setColor(Color.WHITE);
		
		Label finalMessage = new Label("The new file "+ fileName + 
				" has been successfully created.");
		finalMessage.setEffect(dropShadow);
		finishedPane.add(finalMessage, 0, 0);
		
		//add fyi image to background
		InputStream image = ClassLoader.getSystemResourceAsStream("fyi.png");
		Image fyi = new Image(image);
		BackgroundImage myBI= new BackgroundImage(fyi,
			BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
			BackgroundSize.DEFAULT);
		finishedPane.setBackground(new Background(myBI));
				
		//create scene and show on stage
		Scene scene = new Scene(finishedPane);
		primaryStage.setTitle("Hours Extractor");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		try {
			TimeUnit.SECONDS.sleep(5);
		}catch(InterruptedException ie) {
			
		}
		
		System.exit(0);
		
	}
	
	/**
	 * This method takes in a string representation of a month, and it returns an integer
	 * representation.
	 * 
	 * @param month This is the month that will be converted to a number representation
	 * @return This is a number representation of the current month
	 */
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
	
	/**
	 * This is the main method that launches the javafx application.
	 * 
	 * @param args These are the starting arguments for the program.
	 */
	public static void main(String[] args) {
		
		Application.launch(args);

	}

}

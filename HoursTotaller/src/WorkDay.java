
public class WorkDay {

	private float TravelHours = 0;
	private float HouseHours = 0;
	private float CommunityHours = 0;
	private float DocumentationHours = 0;
	private float ConsultHours = 0;
	private int MonthNum = 0;
	private int DayNum = 0;
	private int YearNum = 0;
	private int id = 0;
	private String customerName = "";
	private String mentorName = "";
	
	public WorkDay(int id, float Travel, float House, float Community, float Doc,
			float Consult, int Month, int Day, int Year, String customer, 
			String mentor) {
		
		this.id = id;
		TravelHours = Travel;
		HouseHours = House;
		CommunityHours = Community;
		DocumentationHours = Doc;
		ConsultHours = Consult;
		MonthNum = Month;
		DayNum = Day;
		YearNum = Year;
		customerName = customer;
		mentorName = mentor;
		
	}
	
	
	public int getID() {
		return id;
	}
	
	public float getTravel() {
		return TravelHours;
	}
	
	public float getHouse() {
		return HouseHours;
	}
	
	public float getComm() {
		return CommunityHours;
	}
	
	public float getDoc() {
		return DocumentationHours;
	}
	
	public float getConsult() {
		return ConsultHours;
	}
	
	public String getDate() {
		
		String sessionDate = MonthNum+"/"+DayNum+"/"+YearNum;
		return sessionDate;
		
	}
	
	public int getDay() {
		return DayNum;
	}
	
	public String getMentee() {
		return customerName;
	}
	
	public String getMentor() {
		return mentorName;
	}
	
	
	
	public void setConsult(float hours) {
		ConsultHours = hours;
	}
	
	public void setTravel(float hours) {
		TravelHours = hours; 
	}
	
	public void setHouse(float hours) {
		HouseHours = hours;
	}
	
	public void setComm(float hours) {
		CommunityHours = hours;
	}
	
	public void setDoc(float hours) {
		DocumentationHours = hours;
	}
}

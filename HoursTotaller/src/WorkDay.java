
public class WorkDay {

	private float TravelHours = 0;
	private float HouseHours = 0;
	private float CommunityHours = 0;
	private float DocumentationHours = 0;
	//private float ConsultHours = 0;
	private int MonthNum = 0;
	private int DayNum = 0;
	private int YearNum = 0;
	private String customerName = "";
	private String mentorName = "";
	
	public WorkDay(float Travel, float House, float Community, float Doc,
			int Month, int Day, int Year, String customer, 
			String mentor) {
		
		TravelHours = Travel;
		HouseHours = House;
		CommunityHours = Community;
		DocumentationHours = Doc;
		//ConsultHours = Consult;
		MonthNum = Month;
		DayNum = Day;
		YearNum = Year;
		customerName = customer;
		mentorName = mentor;
		
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
	
	/**public float getConsult() {
		return ConsultHours;
	}**/
	
	public String getDate() {
		
		String sessionDate = MonthNum+"/"+DayNum+"/"+YearNum;
		return sessionDate;
		
	}
	
	public String getMentee() {
		return customerName;
	}
	
	public String getMentor() {
		return mentorName;
	}
}

import javafx.scene.control.Button;


public class IndexBtn {
	
	private int index = 0;
	private Button button;
	
	public IndexBtn(Button bttn, int i) {
		
		button = bttn;
		index = i;
		
	}
	
	public int getIndex() {
		return index;
	}
	
	public Button getButton() {
		return button;
	}
	
}

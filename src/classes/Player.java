package classes;

public class Player {
	private boolean isvalid;
	
	//All these are going to be public because it saves characters
	public String index;
	public int rawnum;
	public String num;
	public String set; // could make this an enum, don't currently care enough to
	public String name;
	public String team; // could make this an enum, don't currently care enough to
	public int points;
	public int year;
	public String pos;
	public String hand; // could make this an enum, don't currently care enough to
	public Chart chart;
	
	public Player() {
		isvalid = false;
	}
	
	public Player(String[] stats) {
		this(stats, new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22});
	}
	

	public Player(String[] stats, int[] positionMap) {
		try {
			
		}
		catch (Exception e) {
			isvalid = false;
		}
	}
	
	public String toString() {
		if(!isvalid) {
			return "invalid player";
		}
		return ""; //TODO: figure out how to represent cards in a concise string
	}
	
	public String print() {
		//TODO: potentially look into printing out ASCII versions of the cards
		return "";
	}
	
	public boolean isValid() {
		return isvalid;
	}
}

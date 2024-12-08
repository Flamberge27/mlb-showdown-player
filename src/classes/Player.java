package classes;

import console_version.DataParser;

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
	public char hand; // could make this an enum, don't currently care enough to
	private String[] real_icons;
	private String[] assumed_icons;
	public Chart chart;
	
	public Player() {
		isvalid = false;
	}
	
	public Player(String[] stats) {
		this(stats, new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22});
	}
	

	public Player(String[] stats, int[] positionMap) {
		/* Essentials:
		 * 	name
		 * 	points
		 *  year
		 *  on base/control
		 *  speed/ip
		 *>  position
		 *  hand
		 *  icons
		 *  chart
		 */
		try {
			name = stats[positionMap[4]];
			points = Integer.parseInt(stats[positionMap[6]]);
			year = Integer.parseInt(stats[positionMap[7]].replaceAll("'",""));
			hand = stats[positionMap[11]].charAt(0);
			
			String[] found_icons = getIconsFromString(stats[positionMap[12]]);
			if(year > 2)
				real_icons = found_icons;
			else
				assumed_icons = found_icons;
			
			this.chart = new Chart(stats, positionMap);
			
			isvalid = true;
		}
		catch (Exception e) {
			System.out.println("Failed to retrieve name/points/year/hand/icons");
			isvalid = false;
		}
		

		/* Non-essentials:
		 * 	rawnum
		 *  num
		 *  set
		 *  team
		 */
		try {
			index = stats[positionMap[0]];
			rawnum = Integer.parseInt(stats[positionMap[1]]);
			num = stats[positionMap[2]];
		}
		catch (Exception e) {
			System.out.println("Failed to retrieve index/num/set/team");
		}
	}
	
	public String[] icons() {
		return real_icons;
	}
	
	public String[] icons(boolean assumeOld) {
		if(assumeOld && year < 3) {
			return assumed_icons;
		}
		
		return real_icons;
	}
	
	public String toString() {
		if(!isvalid) {
			return "invalid player";
		}
		return name + " '0" + year; //TODO: figure out how to represent cards in a concise string
	}
	
	public String print() {
		//TODO: potentially look into printing out ASCII versions of the cards
		return "";
	}
	
	public boolean isValid() {
		return isvalid;
	}

	private String[] getIconsFromString(String raw_in) {
		raw_in = raw_in.replaceAll("\\[", "").replaceAll("\\]", "").trim();
		return raw_in.split(" ");
	}
	
	// private wrapper method
	private int indexof(String in) {
		return DataParser.mapStr(in);
	}
}

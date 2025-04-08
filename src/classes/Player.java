package classes;

import console_version.DataParser;

public abstract class Player {
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
	public char hand; // could make this an enum, don't currently care enough to
	private String[] real_icons;
	private String[] assumed_icons;
	public String[] chart; // array from 0-30, where if result[19] = 'HR', then a 19 results in a homerun
	
	public Player() {
		isvalid = false;
	}
	public Player(String[] stats) {
		this(stats, defaultmap());
	}
	public Player(Player base) {
		isvalid = base.isvalid;
		index = (base.index == null) ? null :new String(base.index);
		rawnum = base.rawnum;
		num = (base.num == null) ? null : new String(base.num);
		set = (base.set == null) ? null :new String(base.set);
		name = (base.name == null) ? null :new String(base.name);
		team = (base.team == null) ? null :new String(base.team);
		points = base.points;
		year = base.year;
		hand = base.hand;
		
		if(base.real_icons != null) {
			real_icons = new String[base.real_icons.length];
			for(int i = 0; i < real_icons.length; i++) {
				real_icons[i] = base.real_icons[i];
			}
		}
		if(base.assumed_icons != null) {
			assumed_icons = new String[base.assumed_icons.length];
			for(int i = 0; i < assumed_icons.length; i++) {
				assumed_icons[i] = base.assumed_icons[i];
			}
		}
		if(base.chart != null) {
			chart = new String[base.chart.length];
			for(int i = 0; i < chart.length; i++) {
				chart[i] = base.chart[i];
			}
		}
	}
	
	/**
	 * Constructor for general players
	 * 
	 * @param stats: array of all stats, generally formed by parsing a card list csv
	 * @param positionMap: in case of inputs that aren't in the expected order, allows remapping
	 */
	public Player(String[] stats, int[] positionMap) {
		/* Essentials:
		 * 	name
		 * 	points
		 *  year
		 *  on base/control
		 *  speed/ip
		 *  position
		 *  hand
		 *  icons
		 *  chart
		 */
		try {
			name = stats[positionMap[4]];
			points = Integer.parseInt(stats[positionMap[6]]);
			year = Integer.parseInt(stats[positionMap[7]].replaceAll("'",""));
			hand = stats[positionMap[11]].charAt(0);
			
			isvalid = true;
		}
		catch (Exception e) {
			System.out.println("Failed to retrieve name/points/year/hand");
			isvalid = false;
		}
		
		try {
			String[] found_icons = getIconsFromString(stats[positionMap[12]]);
			if(year > 2)
				real_icons = found_icons;
			else
				assumed_icons = found_icons;
			
			isvalid = true;
		}
		catch (Exception e) {
			System.out.println("Failed to retrieve icons");
			isvalid = false;
		}
		
		try {
			chart = new String[30];
			
			for(int i = 13; i < 23; i++) {
				addResult(stats[positionMap[i]], DataParser.mapInt(i));
			}
			
			isvalid = true;
		}
		catch (Exception e) {
			System.out.println("Failed to retrieve chart");
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
			System.out.println("Failed to retrieve index/num/set/team for " + this.toString());
		}
	}
	
	public static Player createPlayer(String[] stats) {
		return createPlayer(stats, defaultmap());
	}
	public static Player createPlayer(String[] stats, int[] positionMap) {
		String position = stats[positionMap[10]];
		if(position.equals("Starter") || position.equals("Reliever") || position.equals("Closer")) {
			return new Pitcher(stats, positionMap);
		}
		else {
			return new Batter(stats, positionMap);
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
	
	public int outage() {
		int outs = 0;
		
		for(int i = 1; i < this.chart.length; i++) {
			switch(this.chart[i]) {
			case "PU":
			case "SO":
			case "GB":
			case "FB":
				outs++;
				break;
			}
		}
		
		return outs;
	}
	
	public String toString() {
		if(!isvalid) {
			return "invalid player";
		}
		return name + " ['0" + year + "]"; //TODO: figure out how to represent cards in a concise string
	}
	public abstract String statString();
	public String print() {
		//TODO: potentially look into printing out ASCII versions of the cards
		return "";
	}
	
	public boolean isValid() {
		return isvalid;
	}

	private String[] getIconsFromString(String raw_in) {
		if(raw_in.length() == 0)
			return null;
		raw_in = raw_in.replaceAll("\\[", "").replaceAll("\\]", "").trim();
		return raw_in.split(" ");
	}
	private void addResult(String rawRange, String pos) {
		/* A couple different possibilities here:
		 
		   -	(do nothing)
		   #	(just #, # may be two digits)
		   #-	(just #, # may be two digits)
		   #-#	(all numbers in the given range, inclusive)
		   #+	(# and all future numbers
		 */
		
		// first case, just a dash means do nothing
		if(rawRange == null || rawRange.equals("") || rawRange.equals("-"))
			return;
		
		// condensed all the other cases because I could
		int start_at, end_at;
		if(rawRange.contains("-")) {
			
			// second or third case
			String[] split = rawRange.split("-");
			start_at = Integer.parseInt(split[0]);
			end_at = (split.length == 1) ? start_at : Integer.parseInt(split[1]);
			
		}
		else if(rawRange.indexOf('+') == -1) {
			start_at = Integer.parseInt(rawRange);
			end_at = start_at;
		}
		else {
		
			// assume input is valid, if it isn't we'll crash
			// split to the + in case there are any /n chars (or similar) lying around
			int plus_pos = rawRange.indexOf('+');
			start_at = Integer.parseInt(rawRange.substring(0, plus_pos));
			end_at = chart.length - 1;
		}
		
		for(int change_pos = start_at; change_pos <= end_at; change_pos++) {
			chart[change_pos] = pos;
		}
	}
	
	public abstract void logGame(boolean win);
	public abstract void gameReset();
	public abstract void fullReset();
	public abstract Player copy();
	
	// private wrapper method
	protected static int indexof(String in) {
		return DataParser.mapStr(in);
	}
	private static int[] defaultmap() {
		return new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22};
	}
}

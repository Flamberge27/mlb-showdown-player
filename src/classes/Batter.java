package classes;

import java.util.HashMap;

public class Batter extends Player
{
	public int ob;
	public int speed;
	//public Fielding fielding;
	
	private HashMap<Position, Integer> fielding;
	
	public BatterStats stats;
	
	public Position currentPos;
	
	public Batter()
	{
		super();
	}
	
	public Batter(String[] stats) {
		this(stats, new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22});
	}
	
	public Batter(String[] stats, int[] positionMap) {
		super(stats, positionMap);
		
		this.ob = Integer.parseInt(stats[positionMap[8]]);
		this.speed = parseSpeed(stats[positionMap[9]]);
		//fielding = new Fielding(stats[positionMap[10]]);
		
		fielding = parseFielding(stats[positionMap[10]]);
	}
	
	public int fielding() {
		return fielding(currentPos);
	}
	public int fielding(Position p) {
		if(fielding.containsKey(p)) {
			return fielding.get(p);
		}
		else {
			return -3;
		}
	}
	
	public static HashMap<Position, Integer> parseFielding(String in) {
		// 90% of cases, this is supposed to be really simple
		// unfortunately, those last 10% of cases can be quite tricky
		
		boolean canField = false;
		
		HashMap<Position, Integer> map = initMap();
		
		// e.g. "C+6, 1B+1" splits into two clauses - 'C+6' and '1B+1'
		for(String clause : in.split(",")) {			
			canField = canField || parseClause(clause.replaceAll("\"","").trim(), map);
		}
		
		//as long as we can field, we can play first base at -1. otherwise, we can play it at -2
		if(map.keySet().size() > 0) {
			registerPosition(Position.First, -1, map);
		}
		else {
			registerPosition(Position.First, -2, map);
		}
		
		// System.out.println(this);
		return map;
	}

	private static HashMap<Position, Integer> initMap() {
		HashMap<Position, Integer> map = new HashMap<Position, Integer>();
		
		/*
		map.put(Position.Catcher, -3);
		map.put(Position.First, -3);
		map.put(Position.Second, -3);
		map.put(Position.Short, -3);
		map.put(Position.Third, -3);
		map.put(Position.Left, -3);
		map.put(Position.Center, -3);
		map.put(Position.Right, -3);
		// */
		
		return map;
	}
	private static boolean parseClause(String clause, HashMap<Position, Integer> map) {
		int plusPos, bonus;
		String[] positions;
		
		boolean canField = false;
		
		// get the DH case out of the way
		if(clause.equalsIgnoreCase("DH")) {
			return canField;
		}
					
		plusPos = clause.indexOf("+"); // there should be <=1 '+' per clause
					
		// some '00s and '01s have an implicit +0; e.g. Damon Buford just says OF
		if(plusPos == -1) {
			bonus = 0;
			positions = clause.split("-");
		}
		else {
			bonus = Integer.parseInt(clause.substring(plusPos+1));
			positions = clause.substring(0,plusPos).split("-");
		}
		
		// now parse out the positions
		// 90% of the time, the "split on -" thing just catches LF-RF
		// 10% of the time, cards like Dave Hanson '00 mark multiple positions as 1B-3B+0
		for(String position : positions) {
			canField = canField || parsePosition(position, bonus, map);
		}
		
		return canField;
	}

	private static boolean parsePosition(String pos, int bonus, HashMap<Position, Integer> map) {
		switch(pos) {
			case "C": case "c":
				registerPosition(Position.Catcher, bonus, map);
				break;
			case "1B": case "1b":
				registerPosition(Position.First, bonus, map);
				break;
			case "2B": case "2b":
				registerPosition(Position.Second, bonus, map);
				break;
			case "SS": case "ss":
				registerPosition(Position.Short, bonus, map);
				break;
			case "3B": case "3b":
				registerPosition(Position.Third, bonus, map);
				break;
			case "LF": case "lf":
				registerPosition(Position.Left, bonus, map);
				break;
			case "CF": case "cf":
				registerPosition(Position.Center, bonus, map);
				break;
			case "RF": case "rf":
				registerPosition(Position.Right, bonus, map);
				break;
			case "IF": case "if":
				registerPosition(Position.First, bonus, map);
				registerPosition(Position.Second, bonus, map);
				registerPosition(Position.Short, bonus, map);
				registerPosition(Position.Third, bonus, map);
				break;
			case "OF": case "of":
				registerPosition(Position.Left, bonus, map);
				registerPosition(Position.Center, bonus, map);
				registerPosition(Position.Right, bonus, map);
				break;
			default:
				System.out.println("Could not parse " + pos + " as a position.");
				return false; // note that we return here to avoid setting canField later
		}

		return true; // again, assume that we can field now; otherwise, would have returned
	}
	private static void registerPosition(Position p, int val, HashMap<Position, Integer> map) {
		if(map.containsKey(p)) {
			map.put(p, Math.max(map.get(p), val));
		}
		else {
			map.put(p, val);
		}
	}
	
 	public static int parseSpeed(String speed) {
		// cards from 00/01 have speed listed as A/B/C for 20/15/10
		// this is a quick way to check if our speed is a character
		if(speed.charAt(0) > 64) {
			// A is ascii 65: return 20 - 5*0 = 20
			// B is ascii 66: return 20 - 5*1 = 15
			// C is ascii 67: return 20 - 5*2 = 10
			return 20 - 5*(speed.charAt(0) - 65);
		}
		
		else {
			return Integer.parseInt(speed);
		}
	}

	@Override
	public void gameReset() {
		if(this.stats == null) {
			this.stats = new BatterStats();
		}
	}

	@Override
	public void fullReset() {
		this.stats = new BatterStats();
	}

}

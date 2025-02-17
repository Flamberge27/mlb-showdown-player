package classes;

public class Fielding {
	public int C, first, second, SS, third, LF, CF, RF;
	public boolean canField;
	
	public Fielding(String in) {
		// 90% of cases, this is supposed to be really simple
		// unfortunately, those last 10% of cases can be quite tricky
		
		// -3 means the player cannot play this position
		C = -3;
		first = -3; // apparently, anyone can play 1st at -1
		second = -3;
		SS = -3;
		third = -3;
		LF = -3;
		CF = -3;
		RF = -3;
		canField = false;
		
		// e.g. "C+6, 1B+1" splits into two clauses - 'C+6' and '1B+1'
		for(String clause : in.split(",")) {			
			parseClause(clause.replaceAll("\"","").trim());
		}
		
		//as long as we can field, we can play first base at -1. otherwise, we can play it at -2
		if(this.canField) {
			this.first = Math.max(this.first, -1);
		}
		else {
			this.first = Math.max(this.first, -2);
		}
		
		// System.out.println(this);
	}
	
	private void parseClause(String clause) {
		int plusPos, bonus;
		String[] positions;
		
		// get the DH case out of the way
		if(clause.equalsIgnoreCase("DH")) {
			return;
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
			registerPosition(position, bonus);
		}
	}
	
	private void registerPosition(String pos, int bonus) {
		switch(pos) {
			case "C": case "c":
				C = Math.max(C, bonus);
				break;
			case "1B": case "1b":
				first = Math.max(first, bonus);
				break;
			case "2B": case "2b":
				second = Math.max(second, bonus);
				break;
			case "SS": case "ss":
				SS = Math.max(SS, bonus);
				break;
			case "3B": case "3b":
				third = Math.max(third, bonus);
				break;
			case "LF": case "lf":
				LF = Math.max(LF, bonus);
				break;
			case "CF": case "cf":
				CF = Math.max(CF, bonus);
				break;
			case "RF": case "rf":
				RF = Math.max(RF, bonus);
				break;
			case "IF": case "if":
				first = Math.max(first, bonus);
				second = Math.max(second, bonus);
				SS = Math.max(SS, bonus);
				third = Math.max(third, bonus);
				break;
			case "OF": case "of":
				LF = Math.max(LF, bonus);
				CF = Math.max(CF, bonus);
				RF = Math.max(RF, bonus);
				break;
			default:
				System.out.println("Could not parse " + pos + " as a position.");
				return; // note that we return here to avoid setting canField later
		}

		canField = true; // again, assume that we can field now; otherwise, would have returned
	}

	public String toString() {
		String ret = "";
		if(C > -3)
			ret += "C+" + C + ",";
		if(first > -3)
			ret += "1B" + (first > -1 ? "+" : "") + first + ",";
		if(second > -3)
			ret += "2B+" + second + ",";
		if(SS > -3)
			ret += "SS+" + SS + ",";
		if(third > -3)
			ret += "3B+" + third + ",";
		if(LF > -3)
			ret += "LF+" + LF + ",";
		if(CF > -3)
			ret += "CF+" + CF + ",";
		if(RF > -3)
			ret += "RF+" + RF + ",";
		
		return "[" + ret.substring(0, ret.length()-1) + "]"; //cut off last char; it's a trailing comma
	}
}

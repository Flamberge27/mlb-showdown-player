package classes;

public abstract class CardPrinter {
	public static String printCard(Player p) {
		if(p instanceof Batter) {
			return printBatter((Batter)p);
		}
		if(p instanceof Pitcher) {
			return printPitcher((Pitcher)p);
		}
		return "Invalid player (not pitcher/batter)";
	}
	
	private static String printBatter(Batter b) {
		switch(b.year) {
		case 0:
			return printBatter00(b);
		case 1:
			return printBatter01(b);
		case 2:
			return printBatter02(b);
		case 3:
			return printBatter03(b);
		case 4:
			return printBatter04(b);
		case 5:
			return printBatter05(b);
		default:
			return "Invalid year";
		}
	}
	private static String printPitcher(Pitcher p) {
		switch(p.year) {
		case 0:
			return printPitcher00(p);
		case 1:
			return printPitcher01(p);
		case 2:
			return printPitcher02(p);
		case 3:
			return printPitcher03(p);
		case 4:
			return printPitcher04(p);
		case 5:
			return printPitcher05(p);
		default:
			return "Invalid year";
		}
	}
	
	private static String printBatter00(Batter b) {
		String ret = 
		"/--------0---------0---------0---------0----\\\n" +
		"|  |      |     M L B    S H O W D O W N    |\n" +
		"|  |      |                          ____   |\n" +
		"|  |      |             ,-----------/    \\--|\n" +
		"|  |      |            "   + OB00(b) +   "__|\n" +
		"|  |      |              "  + SP00(b)  +  " |\n" +
		"|  |      |                 " +F00(b, 1)+ " |\n" + // fielding 1 / pts
		"|  |      |                 " +F00(b, 2)+ " |\n" + // fielding 2 / pts
		"|  |      |                 " +F00(b, 3)+ " |\n" + // fielding 3 / pts
		"|  |      |                                 |\n" +
		"|  |      |                                 |\n" +
		"|  |      |                                 |\n" +
		"|  |      |                                 |\n" +
		"|  |      |                                 |\n" +
		"|  |      |                                 |\n" +
		"|  |      |                                 |\n" +
		"|  |      |                   _____________ |\n" +
		"|  |      |                   |     |     | |\n" +
		"|  |      |                   |     |     | |\n" +
		"|  |      |                   |     |     | |\n" +
		"|  |      |                   |     |     | |\n" +
		"|  |      |                   |     |     | |\n" +
		"|  |      |                   |     |     | |\n" +
		"|  |      |                   |     |     | |\n" +
		"|  |      |                   |     |     | |\n" +
		"|  |      |                   |_____|_____| |\n" +
		"|  |      |                                 |\n" +
	   "\\__|______|_________________________________/\n" +
		"";
		return ret;
	}
	private static String printBatter01(Batter b) {
		String ret = "";
		return ret;
	}
	private static String printBatter02(Batter b) {
		String ret = "";
		return ret;
	}
	private static String printBatter03(Batter b) {
		String ret = "";
		return ret;
	}
	private static String printBatter04(Batter b) {
		String ret = "";
		return ret;
	}
	private static String printBatter05(Batter b) {
		String ret = "";
		return ret;
	}
	
	/* --- */
	
	private static String printPitcher00(Pitcher p) {
		String ret = "";
		return ret;
	}
	private static String printPitcher01(Pitcher p) {
		String ret = "";
		return ret;
	}
	private static String printPitcher02(Pitcher p) {
		String ret = "";
		return ret;
	}
	private static String printPitcher03(Pitcher p) {
		String ret = "";
		return ret;
	}
	private static String printPitcher04(Pitcher p) {
		String ret = "";
		return ret;
	}
	private static String printPitcher05(Pitcher p) {
		String ret = "";
		return ret;
	}
	
	// augments on base with a space if necessary
	private static String OB(Batter b) {
		return (b.ob < 10) ? " " + b.ob : "" + b.ob;
	}
	
	private static String SP00(Batter b) {
		String ret = "/ SPEED ";
		switch(b.speed) {
		case 10:
			ret += "C";
			break;
		case 15:
			ret += "B";
			break;
		case 20:
			ret += "A";
			break;
		}
		
		ret += "/";
		ret += b.hand;
		ret += "/";
		
		return UL(ret) + "______";
	}
	public static String UL(String s) {
		// This isn't working consistently, but I think that's just quirks of the eclipse console
		String ret = "";
		for(int i = 0; i < s.length(); i++) {
			ret += "\u0332";
			ret += s.charAt(i);
		}
		return ret;
	}
	

	private static String OB00(Batter b) {
		return UL("/ ON - BASE  \\ " + OB(b) + " /");
	}
	private static String F00(Batter b, int n) {
		String[] splat = b.fieldingString.split(",");
		if(n > splat.length + 1) {
			return "               ";
		}
		
		String ret = "/ " + ((n <= splat.length) ? splat[n-1] + " /" :  b.points + " pt.");
		
		ret = UL(ret) + ((n == splat.length + 1) ? "/" : "");
		
		// append underscores for next row if necessary
		int nextlen = -1;
		if(n < splat.length) {
			nextlen = (" " + splat[n] + " /").length();
		}
		else if(n == splat.length) {
			nextlen = (" " + b.points + " pt./").length();
		}
		
		for(int pos = ret.replaceAll("\\u0332","").length(); pos < 15; pos++) {
			if(pos < nextlen) {
				ret = "_" + ret;
			}
			else {
				ret = " " + ret;
			}
		}
		
		return ret;
	}
}

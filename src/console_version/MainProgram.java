package console_version;

import java.util.Hashtable;

import classes.*;
import data.LineupGenerator;

public class MainProgram {
	
	public static void main(String[] args) {
		
		loadAllPlayers();
	}
	
	public static void loadAllPlayers() {
		Hashtable<String, Player> allPlayers = DataParser.parseData();
		
		Team perfectLineup = LineupGenerator.perfectLineup(allPlayers);
		System.out.println(perfectLineup.toString());
	}
	
	public static void testPlayerGen() {
		String[] testin = {"","10","10-1","UL","Test Player","Test Team",
				"50","'06","10","16","LF-RF+1,2B+2","L","[ R SB ]",
				"-","1-2","3-","4-6","7-12","13-16","17","18-19","20","21+"};
		
		Player test_player = new Batter(testin);
		System.out.println(test_player);
	}
	
	public static void testBuggedPlayer() {
		String[] testin = {"'05 127-1 UL","127","127-1","UL","Adrian Beltre","Mariners",
				"740","'05","13","12","3B+2","R","",
				"","1-","2-4","5-7","8-9","10-15","-","16-17","-","18+"};
		Player test_player = new Batter(testin);
		System.out.println(test_player);
	}
}

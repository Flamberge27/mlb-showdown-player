package console_version;

import java.util.Hashtable;

import classes.*;
import data.LineupGenerator;

public class MainProgram {
	
	public static void main(String[] args) {
		Hashtable<String, Player> allPlayers = DataParser.parseData();
		
		System.out.println(CardPrinter.printCard(allPlayers.get("'00 453 1st")));
		
		//loadAllPlayers();
	}
	
	public static void loadAllPlayers() {
		Hashtable<String, Player> allPlayers = DataParser.parseData();
		
		Team pl = LineupGenerator.perfectLineup(allPlayers);
		//System.out.println(pl.toString());
		
		Team fast = LineupGenerator.fastLineup(allPlayers);
		//Team mets = LineupGenerator.metsLineup(allPlayers);
		//Team fivek = LineupGenerator.constrainedLineup(allPlayers);
		
		GameManager gm = new GameManager(pl, fast);
		gm.output = new FakeOutput();
		
		gm.playGames(10000);
		gm.output = new ConsoleOutput();
		
		gm.displayStats();
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

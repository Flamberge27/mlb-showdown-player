package console_version;

import classes.*;

public class MainProgram {
	
	public static void main(String[] args) {
		String[] testin = {"","10","10-1","UL","Test Player","Test Team",
				"50","'06","10","16","LF/RF+1,IF+1","L","[ R SB ]",
				"-","1-2","3-","4-6","7-12","13-16","17","18-19","20","21+"};
		
		Player test_player = new Player(testin);
		System.out.println(test_player);
		
	}

}

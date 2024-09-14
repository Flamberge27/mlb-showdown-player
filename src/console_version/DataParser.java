package console_version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import classes.*;

public class DataParser {

	
	public static ArrayList<Player> parseData() {
		parseData("card_list.csv");
	}
	
	public static ArrayList<Player> parseData(String filename) {
		try {
			String src_dir = new File(".").getCanonicalPath();
			String file = src_dir + "\\src\\data\\" + filename;
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String[] header = br.readLine().split(",");
			
			// This step is mostly a formality that can remap columns that are in an unexpected order
			int[] positions = new int[23];
			boolean inorder = true;
			for(int pos = 0; pos < header.length; pos++) {
				if(mapStr(header[pos]) != pos) {
					inorder = false;
				}
				positions[mapStr(header[pos])] = pos; // So that positions[2]
			}
			
			ArrayList<Player> playerList = new ArrayList<Player>();
			String line;
			while( (line = br.readLine()) != null ) {
				if(inorder) {
					Player p = new Player(line.split(","));
				}
				else {
					Player p = new Player(line.split(","), positions);
				}
			}
			
			return playerList;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
		
	}
	
	public static void parr(String[] arr) {
		System.out.print("[");
		for(String s : arr) {
			System.out.print(s + " ");
		}
		System.out.println("]");
	}
	
	private static int mapStr(String in) {
		switch(in) {
			case "Index":
				return 0;
			case "Raw #":
				return 1;
			case "#":
				return 2;
			case "Set":
				return 3;
			case "Name":
				return 4;
			case "Team":
				return 5;
			case "Pts.":
				return 6;
			case "Yr.":
				return 7;
			case "OB/C":
				return 8;
			case "Spd/IP":
				return 9;
			case "Pos":
				return 10;
			case "H":
				return 11;
			case "Icon":
				return 12;
			case "PU":
				return 13;
			case "SO":
				return 14;
			case "GB":
				return 15;
			case "FB":
				return 16;
			case "W":
				return 17;
			case "S":
				return 18;
			case "S+":
				return 19;
			case "DB":
				return 20;
			case "TR":
				return 21;
			case "HR":
				return 22;
			default:
				return 4; // When in doubt, shove unexpected input into the name column
	}
		
		
	}
}

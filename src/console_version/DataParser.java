package console_version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;

import classes.Player;

public class DataParser {

	
	public static Hashtable<String, Player> parseData() {
		return parseData("card_list.csv");
	}
	
	public static Hashtable<String, Player> parseData(String filename) {
		try {
			String src_dir = new File(".").getCanonicalPath();
			String file = src_dir + "\\src\\data\\" + filename;
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String[] header = csvSplit(br.readLine());
			
			// This step is mostly a formality that can remap columns that are in an unexpected order
			int[] positions = new int[23];
			boolean inorder = true;
			for(int pos = 0; pos < header.length; pos++) {
				if(mapStr(header[pos]) != pos) {
					inorder = false;
				}
				positions[mapStr(header[pos])] = pos; // So that positions[2]
			}
			
			Hashtable<String, Player> playerList = new Hashtable<String, Player>();
			String line;
			Player p;
			while( (line = br.readLine()) != null ) {
				if(inorder) {
					p = Player.createPlayer(csvSplit(line));
				}
				else {
					p = Player.createPlayer(csvSplit(line), positions);
				}
				playerList.put(p.index, p);
			}
			
			return playerList;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
		
	}
	
	public static String[] csvSplit(String csv_line) {
		String[] ret = new String[23]; // we know ahead of time that this should be 23 columns deep
		
		int start_of_clause = 0;
		boolean inQuotes = false;
		int ret_pos = 0;
		
		try {
			for (int current = 0; current < csv_line.length(); current++) {
			    if (csv_line.charAt(current) == '\"') {
			    	inQuotes = !inQuotes; // toggle state
			    }
			    else if (csv_line.charAt(current) == ',' && !inQuotes) {
			    	// I'm letting us crash out here if we have too many commas, for error-detection in the input
			    	ret[ret_pos] = csv_line.substring(start_of_clause, current);
			        start_of_clause = current + 1;
			        ret_pos++;
			    }
			    else if(current == csv_line.length() - 1) {
			    	ret[ret_pos] = csv_line.substring(start_of_clause);
			    }
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Too many entries in this line: " + csv_line);
		}
		
		return ret;
	}
	
	public static void parr(String[] arr) {
		System.out.print("[");
		for(String s : arr) {
			System.out.print(s + " ");
		}
		System.out.println("]");
	}
	
	
	// probably an easier way to do this - enums with values?
	public static int mapStr(String in) {
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
	public static String mapInt(int in) {
		switch(in) {
			case 0:
				return "Index";
			case 1:
				return "Raw #";
			case 2:
				return "#";
			case 3:
				return "Set";
			case 4:
				return "Name";
			case 5:
				return "Team";
			case 6:
				return "Pts.";
			case 7:
				return "Yr.";
			case 8:
				return "OB/C";
			case 9:
				return "Spd/IP";
			case 10:
				return "Pos";
			case 11:
				return "H";
			case 12:
				return "Icon";
			case 13:
				return "PU";
			case 14:
				return "SO";
			case 15:
				return "GB";
			case 16:
				return "FB";
			case 17:
				return "W";
			case 18:
				return "S";
			case 19:
				return "S+";
			case 20:
				return "DB";
			case 21:
				return "TR";
			case 22:
				return "HR";
		default:
				return "Name"; // When in doubt, shove unexpected input into the name column
		}
	}
}

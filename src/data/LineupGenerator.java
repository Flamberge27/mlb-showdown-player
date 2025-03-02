package data;

import java.util.ArrayList;
import java.util.Hashtable;

import AI.Level1AI;
import classes.Player;
import classes.Team;

public abstract class LineupGenerator {
	public static Team perfectLineup(Hashtable<String, Player> allPlayers) {
		ArrayList<String> indices = new ArrayList<String>();
		
		indices.add("'05 175 CC"); // Nolan Ryan
		indices.add("'05 195 UL"); // Johan Santana
		indices.add("'04 124-1 CC"); // Warren Spahn
		indices.add("'02 112 SS"); // Pedro Martinez '97

		indices.add("'04 116-2 UL"); // Rollie Fingers
		indices.add("'04 150 UL"); // Octavio Dotel
		
		indices.add("'04 177 UL"); // Eric Gagne
		indices.add("'02 20 ASG"); // Kazuhiro Sasaki
		
		indices.add("'02 108 SS"); // Mike Piazza '97
		indices.add("'02 117 SS"); // Frank Thomas '94
		indices.add("'02 114 SS"); // Roberto Alomar '93
		indices.add("'04 104-1 UL"); // Alex Rodriguez
		indices.add("'04 125-1 CC"); // Mike Schmidt
		indices.add("'03 252 UL"); // Barry Bonds
		indices.add("'02 120 SS"); // Ken Griffey Jr. '97
		indices.add("'02 106 SS"); // Rickey Henderson '90
		indices.add("'02 98 SS"); // Jeff Bagwell '94
		
		indices.add("'05 172 CC"); // Reggie Jackson
		indices.add("'03 124 CC"); // Rod Carew
		indices.add("'02 111 SS"); // Larry Walker '97
		indices.add("'04 120 CC"); // Willie McCovey
		
		Team team = makeTeamFromIndexArray(allPlayers, indices);
		team.name = "The Perfect Lineup";
		return team;
	}

	public static Team fastLineup(Hashtable<String, Player> allPlayers) {
		ArrayList<String> indices = new ArrayList<String>();
		
		indices.add("'02 112 SS"); // Pedro Martinez '97
		
		indices.add("'04 177 UL"); // Eric Gagne
		indices.add("'02 20 ASG"); // Kazuhiro Sasaki
		
		indices.add("'03 114 SS"); // Ivan Rodriguez
		indices.add("'05 173 CC"); // Rod Carew
		indices.add("'04 109-1 UL"); // Craig Biggio
		indices.add("'04 117-1 UL"); // Luis Aparicio
		indices.add("'02 299 UL"); // Mark McLemore
		indices.add("'03 142 RS"); // Ichiro Suzuki '01
		indices.add("'02 119 SS"); // Kenny Lofton '93
		indices.add("'02 106 SS"); // Rickey Henderson '90
		indices.add("'04 118-1 UL"); // Lou Brock
		
		Team team = makeTeamFromIndexArray(allPlayers, indices);
		team.name = "The Fastest Lineup";
		return team;
	}
	
	public static Team metsLineup(Hashtable<String, Player> allPlayers) {
		ArrayList<String> indices = new ArrayList<String>();
		
		// Pitchers
		indices.add("'04 124 CC");
		indices.add("'04 39-1 UL");
		indices.add("'03 106 SS");
		
		indices.add("'04 105-2 UL");
		indices.add("'04 220 UL");
		indices.add("'02 23-1 UL");
		indices.add("'04 222 UL");
		indices.add("'03 191 UL");
		indices.add("'04 216 UL");
		indices.add("'05 32-1 UL");
		indices.add("'02 11-2 UL");
		indices.add("'04 101-1 UL");
		
		Team team = makeTeamFromIndexArray(allPlayers, indices);
		team.name = "The All-Mets Lineup";
		return team;
	}
	
	public static Team constrainedLineup(Hashtable<String, Player> allPlayers) {
		ArrayList<String> indices = new ArrayList<String>();
		
		// Starters
		indices.add("'04 124-1 CC");
		indices.add("'02 44-1 UL");
		indices.add("'02 10-2 UL");
		indices.add("'02 108-2 UL");
		
		// Relievers/Closers
		indices.add("'02 84-2 UL");
		indices.add("'00 447 1st");
		indices.add("'00 202 1st");
		
		// Main lineup
		indices.add("'04 32-2 UL");
		indices.add("'05 4-2 UL");
		indices.add("'04 109-1 UL");
		indices.add("'04 110-3 UL");
		indices.add("'05 83-2 UL");
		indices.add("'04 117 CC");
		indices.add("'02 120 SS");
		indices.add("'05 141-1 UL");
		indices.add("'02 54-1 UL");
		
		// Subs
		indices.add("'05 110-2 UL");
		indices.add("'04 92-3 UL");
		indices.add("'04 129 UL");
		indices.add("'04 28 P");
		indices.add("'05 11-2 UL");
		indices.add("'04 36 P");
		indices.add("'05 301 UL");
		indices.add("'02 207 UL");
		indices.add("'05 78-2 UL");
		
		Team team = makeTeamFromIndexArray(allPlayers, indices);
		team.name = "5000-point team";
		return team;
	}
	
	private static Team makeTeamFromIndexArray(Hashtable<String, Player> allPlayers, ArrayList<String> indices) {
		ArrayList<Player> players = new ArrayList<>();
		indices.forEach( (index) -> players.add(allPlayers.get(index)));
		return new Team(players, new Level1AI());
	}
}

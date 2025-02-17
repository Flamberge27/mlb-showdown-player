package data;

import java.util.ArrayList;
import java.util.Hashtable;

import classes.Player;
import classes.Team;

public abstract class LineupGenerator {
	public static Team perfectLineup(Hashtable<String, Player> allPlayers) {
		ArrayList<Player> lineup = new ArrayList<Player>();
		
		lineup.add(allPlayers.get("'05 175 CC")); // Nolan Ryan
		lineup.add(allPlayers.get("'05 195 UL")); // Johan Santana
		lineup.add(allPlayers.get("'04 124-1 CC")); // Warren Spahn
		lineup.add(allPlayers.get("'02 112 SS")); // Pedro Martinez '97

		lineup.add(allPlayers.get("'04 116-2 UL")); // Rollie Fingers
		lineup.add(allPlayers.get("'04 150 UL")); // Octavio Dotel
		
		lineup.add(allPlayers.get("'04 177 UL")); // Eric Gagne
		lineup.add(allPlayers.get("'02 20 ASG")); // Kazuhiro Sasaki
		
		lineup.add(allPlayers.get("'02 108 SS")); // Mike Piazza '97
		lineup.add(allPlayers.get("'02 117 SS")); // Frank Thomas '94
		lineup.add(allPlayers.get("'02 114 SS")); // Roberto Alomar '93
		lineup.add(allPlayers.get("'04 104-1 UL")); // Alex Rodriguez
		lineup.add(allPlayers.get("'04 125-1 CC")); // Mike Schmidt
		lineup.add(allPlayers.get("'03 252 UL")); // Barry Bonds
		lineup.add(allPlayers.get("'02 120 SS")); // Ken Griffey Jr. '97
		lineup.add(allPlayers.get("'02 106 SS")); // Rickey Henderson '90
		lineup.add(allPlayers.get("'02 98 SS")); // Jeff Bagwell '94
		
		lineup.add(allPlayers.get("'05 172 CC")); // Reggie Jackson
		lineup.add(allPlayers.get("'03 124 CC")); // Rod Carew
		lineup.add(allPlayers.get("'02 111 SS")); // Larry Walker '97
		lineup.add(allPlayers.get("'04 120 CC")); // Willie McCovey
		
		return new Team(lineup);
	}
}

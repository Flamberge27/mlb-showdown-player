package classes;

import java.util.ArrayList;

import AI.*;

public class Team {
	public ArrayList<Pitcher> bullpen; // list of ALL pitchers
	public Pitcher onMound; // current pitcher
	
	public ArrayList<Batter> roster; // list of ALL batters
	public ArrayList<Batter> lineup; // current list of 9 hitters/fielders
	public ArrayList<Batter> subs; // current list of remaining substitutions
	public int atBat; // which spot in the lineup is at bat
	
	public Player at_catcher, at_first, at_second, at_short, at_third, at_left, at_center, at_right, DH;
	
	public AI ai;
	public String name;
	
	public Team(ArrayList<Player> players) {
		this(players, new BarebonesAI());
	}
	public Team(ArrayList<Player> players, AI ai) {
		bullpen = new ArrayList<>();
		roster = new ArrayList<>();
		lineup = new ArrayList<>();
		subs = new ArrayList<>();
		
		this.ai = ai;
		
		for(Player p: players) {
			if(p instanceof Pitcher)
				bullpen.add((Pitcher)p);
			else
				roster.add((Batter)p);
		}
		
		name = "";
		
		ai.ChooseStartingPitcher(this);
		ai.ChooseStartingLineup(this);
		ai.AssignBases(this);
	}
	
	public String toString() {
		String ret = "============\n";
		ret += (name == null || name.length() == 0) ? "No name" : name + ":";
		ret += "\n-----\n";
		for(Pitcher p: bullpen) {
			if(p == onMound)
				ret += "> ";
			ret += p + "\n";
		}
		ret += "-----\n";
		for(Batter b: lineup) {
			if(b == lineup.get(atBat))
				ret += "> ";
			ret += b + "\n";
		}
		ret += "\n";
		for(Batter b: subs)
			ret += b + "\n";
		return ret;
	}
}

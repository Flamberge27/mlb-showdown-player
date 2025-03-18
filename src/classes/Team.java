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
	
	public Batter at_catcher, at_first, at_second, at_short, at_third, at_left, at_center, at_right, DH;
	
	private OffenseAI offenseAI;
	private DefenseAI defenseAI;
	public String name;
	
	public boolean strategyAvailable;
	
	public Team(ArrayList<Player> players) {
		this(players, new BarebonesAI(), new BarebonesAI());
	}
	public Team(ArrayList<Player> players, OffenseAI oai, DefenseAI dai) {
		bullpen = new ArrayList<>();
		roster = new ArrayList<>();
		lineup = new ArrayList<>();
		subs = new ArrayList<>();
		
		oai.setTeam(this);
		dai.setTeam(this);
		this.offenseAI = oai;
		this.defenseAI = dai;
		
		for(Player p: players) {
			if(p instanceof Pitcher)
				bullpen.add((Pitcher)p);
			else
				roster.add((Batter)p);
		}
		
		name = "";
		strategyAvailable = false;

		this.offenseAI.ChooseStartingLineup();
		this.defenseAI.ChooseStartingPitcher();
		this.defenseAI.AssignBases();
	}
	
	/** Team methods
	 ** 
	 **/
	public int infielding() {
		return this.at_first.fielding.first +
				this.at_second.fielding.second +
				this.at_short.fielding.SS +
				this.at_third.fielding.third;
	}
	public int outfielding() {
		return this.at_left.fielding.LF +
				this.at_center.fielding.CF +
				this.at_right.fielding.RF;
	}
	
	public Batter batting() {
		return lineup.get(atBat);
	}
	
	/** AI methods, mostly wrappers
	 ** 
	 **/
	public void playStrategyCard(GameManager g, int stage) {
		if(!this.strategyAvailable) {
			return;
		}
		
		// true = used strategy, so whether we still have an availability is 
		//strategyAvailable = !this.ai.resolveStrategyCards(g, stage);
	}
	public void pinchHit(GameManager g) {
		this.offenseAI.determinePinchHit(g);
	}
	public void relievePitcher(GameManager g) {
		this.defenseAI.determineReliever(g);
	}
	public boolean forceWalk(GameManager g) {
		return this.defenseAI.forceWalk(g);
	}
	public boolean forceBunt(GameManager g) {
		return this.offenseAI.forceBunt(g);
	}
	public int determineStealers(GameManager g) {
		// %2->0 means steal 2->3, %3 == 0 means steal 3->Home
		return this.offenseAI.determineStealers(g);
	}
	public int determineThrow(GameManager g, int steal_num) {
		return this.defenseAI.determineThrow(g, steal_num);
	}
	
	/** Misc methods
	 * 
	 * @param index1
	 * @param index2
	 */
	public void swap(int index1, int index2) {
		lineup.set(index1, lineup.set(index2, lineup.get(index1)));
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

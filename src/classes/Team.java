package classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AI.BarebonesAI;
import AI.DefenseAI;
import AI.OffenseAI;

public class Team {
	public List<Pitcher> bullpen; // list of ALL pitchers
	public Pitcher onMound; // current pitcher
	
	public List<Batter> roster; // list of ALL batters
	public List<Batter> lineup; // current list of 9 hitters/fielders
	public List<Batter> subs; // current list of remaining substitutions
	public int atBat; // which spot in the lineup is at bat
	
	private OffenseAI offenseAI;
	private DefenseAI defenseAI;
	public String name;
	
	public boolean strategyAvailable;
	
	public Map<Position, Batter> fieldPositions;
	
	public Team(ArrayList<Player> players) {
		this(players, new BarebonesAI(), new BarebonesAI(), null, null);
	}
	public Team(ArrayList<Player> players, OffenseAI oai, DefenseAI dai) {
		this(players, oai, dai, null, null);
	}
	public Team(ArrayList<Player> players, OffenseAI oai, DefenseAI dai, int[] lineupOrder, int[] fieldingOrder) {
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
		this.fieldPositions = new HashMap<Position, Batter>();
		
		//---\\
		if(lineupOrder == null) {
			this.offenseAI.ChooseStartingLineup();	
		}
		else {
			this.setLineupFromArr(lineupOrder);
		}
		//---\\
		if(fieldingOrder == null) {
			this.defenseAI.AssignBases();
		}
		else {
			this.setFieldingFromArr(fieldingOrder);
		}
		//---\\
		this.defenseAI.ChooseStartingPitcher();
	}
	
	public Batter at(Position p) {
		return fieldPositions.get(p);
	}
	
	/** Team methods
	 ** 
	 **/
	public int infielding() {
		return this.at(Position.First).fielding() +
				this.at(Position.Second).fielding() +
				this.at(Position.Short).fielding() +
				this.at(Position.Third).fielding();
	}
	public int outfielding() {
		return this.at(Position.Left).fielding() +
				this.at(Position.Center).fielding() +
				this.at(Position.Right).fielding();
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
	public Batter setFielder(Position position, Batter batter) {
		if(this.fieldPositions == null) {
			fieldPositions = new HashMap<Position, Batter>();
		}
		
		Batter prev = null;
		
		if(fieldPositions.containsKey(position)) {
			prev = fieldPositions.get(position);
		}
		// separate if clause, on the off chance that fieldPositions(p) exists but is null
		if(prev != null) { 
			prev.currentPos = null;
		}

		batter.currentPos = position;
		fieldPositions.put(position, batter);
		
		return prev;
	}
	
	
	private void setLineupFromArr(int[] arr) {
		this.lineup = Arrays.asList(roster.get(arr[0]),
									roster.get(arr[1]),
									roster.get(arr[2]),
									roster.get(arr[3]),
									roster.get(arr[4]),
									roster.get(arr[5]),
									roster.get(arr[6]),
									roster.get(arr[7]),
									roster.get(arr[8]));
	}
	private void setFieldingFromArr(int[] arr) {
		this.setFielder(Position.Catcher, roster.get(arr[0]));
		this.setFielder(Position.First, roster.get(arr[1]));
		this.setFielder(Position.Second, roster.get(arr[2]));
		this.setFielder(Position.Short, roster.get(arr[3]));
		this.setFielder(Position.Third, roster.get(arr[4]));
		this.setFielder(Position.Left, roster.get(arr[5]));
		this.setFielder(Position.Center, roster.get(arr[6]));
		this.setFielder(Position.Right, roster.get(arr[7]));
	}
	
	/** Misc methods
	 * 
	 */
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

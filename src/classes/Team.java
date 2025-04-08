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
	public List<Pitcher> starters;
	public List<Pitcher> relievers;
	public Pitcher onMound; // current pitcher
	
	public List<Batter> roster; // list of ALL batters
	public List<Batter> lineup; // current list of 9 hitters/fielders
	public List<Batter> subs; // current list of remaining substitutions
	public int atBat; // which spot in the lineup is at bat
	
	private List<Player> allPlayers;
	
	private int[] forcedlineup, forcedfielding;
	
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
		starters = new ArrayList<>();
		relievers = new ArrayList<>();
		roster = new ArrayList<>();
		lineup = new ArrayList<>();
		subs = new ArrayList<>();
		
		allPlayers = players;
		
		oai.setTeam(this);
		dai.setTeam(this);
		this.offenseAI = oai;
		this.defenseAI = dai;
		
		for(Player p: players) {
			if(p instanceof Pitcher) {
				Pitcher pi = (Pitcher) p;
				if(pi.position == Position.Starter) {
					starters.add(pi);
				}
				else {
					relievers.add(pi);
				}
			}
			else {
				roster.add((Batter)p);
			}
		}
		
		name = "";
		strategyAvailable = false;
		this.fieldPositions = new HashMap<Position, Batter>();
		
		//---\\
		forcedlineup = lineupOrder;		// can be null
		forcedfielding = fieldingOrder; // can be null
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
		Pitcher p = this.defenseAI.determineReliever(g);
		if(p == null) {
			return;
		}
		
		onMound = p;
		
		// this means we're not setting playedThisGame for starters, but we shouldn't ever need to do that, so...
		if(g.rules.reliever_rotation == RelieverRotation.TwoGames) {
			onMound.playedThisGame = true;
		}
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

	public Batter at(Position p) {
		return fieldPositions.get(p);
	}
	
	public void gameReset(GameManager g) {
		this.atBat = 0;
		strategyAvailable = false;
		
		//=====
		for(Player p: allPlayers) {
			p.gameReset();
		}
		
		//---
		if(forcedlineup != null) {
			this.setLineupFromArr(forcedlineup);
		}
		else {
			this.offenseAI.ChooseStartingLineup();	
		}
		//---
		if(forcedfielding != null) {
			this.setFieldingFromArr(forcedfielding);
		}
		else {
			this.defenseAI.AssignBases();
		}
		this.pickStarter(g.rules.starter_rotation);
	}
	
	private void pickStarter(StarterRotation rotation) {
		if(rotation == null || rotation == StarterRotation.PickAny) {
			defenseAI.ChooseStartingPitcher();
		}
		else if(rotation == StarterRotation.Random) {
			onMound = starters.get((int)(starters.size() * Math.random()));
		}
		else if(rotation == StarterRotation.Rotate) {
			if(onMound == null) {
				onMound = starters.getFirst();
			}
			else {
				onMound = starters.get((starters.indexOf(onMound) + 1) % starters.size());
			}
		}
		else {
			System.out.println("!!! Unexpected starter rotation");
		}
	}
	
	public void logGame(boolean win) {
		for(Player p: allPlayers) {
			p.logGame(win);
		}
	}
	
	/** Misc methods
	 * 
	 */
	public String toString() {
		String ret = "============\n";
		ret += (name == null || name.length() == 0) ? "No name" : name + ":";
		ret += "\n-----\n";
		for(Pitcher p: starters) {
			if(p == onMound)
				ret += "> ";
			ret += p + "\n";
		}
		for(Pitcher p: relievers) {
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
	
	public String displayStats() {
		int longest_name = -1;
		for(Player p: allPlayers) {
			longest_name = Math.max(p.name.length(), longest_name);
		}
		
		String ret = "============\n";
		ret += (name == null || name.length() == 0) ? "No name" : name + ":";
		ret += "\n-----\n";
		
		ret += "Pitcher:" + tabs(longest_name, "Pitcher:") + "\tW\tIP\tERA\tFIP\tWHIP\n";
		for(Pitcher p: starters) {
			if(p.stats.IP > 0) {
				ret += tabs(longest_name, p.name) + p.statString()+ "\n";
			}
		}
		for(Pitcher p: relievers) {
			if(p.stats.IP > 0) {
				ret += tabs(longest_name, p.name) + p.statString()+ "\n";
			}
		}
		ret += "-----\n";
		ret += "Batter:" + tabs(longest_name, "Batter:") + "\tW\tAB\tOBP\tSLG\tSB\n";
		for(Batter b: roster) {
			if(b.stats.AB > 0) {
				ret += tabs(longest_name, b.name) + b.statString() + "\n";	
			}
		}
		ret += "\n";
		
		return ret;
	}
	
	private String tabs(int longest_name, String s) {
		int length = longest_name / 8 + 1;
		
		String ret = "";
		for(int cur = s.length()/8 + 1; cur < length; cur++) {
			ret += "\t";
		}
		
		return ret;
	}
}

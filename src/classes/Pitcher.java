package classes;

import data.CustomFunctions;

public class Pitcher extends Player
{
	public int control;
	public int ip;
	public Position position;
	
	// can be used for multi-game leagues, or for ejecting via strategy card
	public boolean canPlay = true;
	public boolean playedThisGame = false;
	private boolean playedPreviousGame = false;
	
	private int innings_pitched;
	private int runs_allowed;
	
	public PitcherStats stats;
	
	public Pitcher()
	{
		super();
	}
	public Pitcher(String[] stats) {
		super(stats);
	}
	public Pitcher(String[] stats, int[] positionMap) {
		super(stats, positionMap);
		
		this.control = Integer.parseInt(stats[positionMap[8]]);
		this.ip = Integer.parseInt(stats[positionMap[9]]);
		this.position = Position.valueOf(stats[positionMap[10]]);
	}
	public Pitcher(Pitcher p) {
		super(p);
		control = p.control;
		ip = p.ip;
		
		position = p.position;
		canPlay = true;
		innings_pitched = 0;
		runs_allowed = 0;
		
		stats = new PitcherStats();
	}
	
	@Override
	public void gameReset() {
		if(playedThisGame) {
			playedPreviousGame = true;
		}
		playedThisGame = false;
		
		if(this.position != Position.Starter) {
			this.canPlay = true;
		}
		
		this.innings_pitched = 0;
		this.runs_allowed = 0;
		
		if(this.stats == null) {
			this.stats = new PitcherStats();
		}
		this.stats.G++;
	}
	public void fullReset() {
		
		playedThisGame = false;
		playedPreviousGame = false;
		canPlay = true;
		
		this.innings_pitched = 0;
		this.runs_allowed = 0;
		
		this.stats = new PitcherStats();
	}
	
	public int current_control() {
		int con = control;
		if(position != Position.Starter && playedPreviousGame) {
			con = 0;
		}
		
		if(innings_pitched > ip) {
			con -= (innings_pitched - ip);
		}
		
		if(runs_allowed >= 3) {
			con -= runs_allowed / 3; // integer division to cut off remainder
		}
		
		return con;
	}
	
	public void logRun() {
		this.runs_allowed++;
		this.stats.ER++;
	}
	public void logInning() {
		this.innings_pitched++;
		this.stats.IP++;
	}
	public void logInning(int my_batters, int total_batters) {
		this.innings_pitched++;
		this.stats.IP += my_batters * 1.0 / total_batters;
	}
	
	public int runsAllowed() {
		return runs_allowed;
	}
	
	@Override
	public Pitcher copy() {
		Pitcher p = new Pitcher(this);
		
		return p;
	}
	
	public String statString() {
		
		String ret = name + "\t";
		ret += stats.W + "\t";
		ret += CustomFunctions.limitTo5(stats.IP) + "\t";
		ret += CustomFunctions.limitTo5(stats.ERA()) + "\t";
		ret += CustomFunctions.limitTo5(stats.FIP()) + "\t";
		ret += CustomFunctions.limitTo5(stats.WHIP());
		
		return ret;
	}
	@Override
	public void logGame(boolean win) {
		if(win) {
			stats.W++;
		}
		else {
			stats.L++;
		}
		
	}
}

package classes;

public class GameRules {
	
	public boolean activeIcons;
	public boolean allowStrategyCards;
	
	public int maxPlayers;
	public int reqStarters;
	public int lineupLength;
	
	public int outs;
	public int innings;
	
	// TODO: add support for 3+ way games
	// realistically, maybe use this to handle leagues?
	// public int teams;
	
	public String home_away;
	public StarterRotation starter_rotation;
	public RelieverRotation reliever_rotation;
	public String al_nl;

	public boolean diverseOuts;
	public boolean canSteal;
	public boolean canPinch;
	public boolean canRelieve;
	public boolean canIBB;
	public boolean canBunt;
	
	public double ip_factor_per_inning;
	public double ip_factor_runs_per;
	public double backup_pt_factor;
	
	private static GameRules Default() {
		GameRules g = new GameRules();
		
		g.maxPlayers = 25;
		g.reqStarters = 4;
		g.lineupLength = 9;
		
		g.outs = 3;
		g.innings = 9;
		
		g.home_away = "random";
		g.starter_rotation = null;
		g.al_nl = "al";
		
		g.starter_rotation = StarterRotation.Rotate;
		g.reliever_rotation = RelieverRotation.Free;
		
		return g;
	}
	
	public static GameRules Basic2004() {
		GameRules g = Default();
		
		g.activeIcons = true;
		g.allowStrategyCards = false;
		
		g.canSteal = false;
		g.diverseOuts = false;
		g.canPinch = false;
		g.canRelieve = false;
		g.canIBB = false;
		g.canBunt = false;
		
		g.ip_factor_per_inning = 0.0; // no penalty in basic rules
		g.ip_factor_runs_per = 0.0;
		g.backup_pt_factor = 0.2;
		
		return g;
	}
	
	public static GameRules Advanced2004() {
		GameRules g = Basic2004();
		
		g.activeIcons = true;
		g.allowStrategyCards = false;
		
		g.canSteal = true;
		g.diverseOuts = true;
		
		g.canPinch = true;
		g.canRelieve = true;
		g.ip_factor_per_inning = 1.0;
		
		return g;
	}
	
	public static GameRules Expert2004() {
		GameRules g = Advanced2004();
		
		g.activeIcons = false;
		g.allowStrategyCards = true;
		
		g.canIBB = true;
		g.canBunt = true;
		
		g.ip_factor_runs_per = 3;
		g.reliever_rotation = RelieverRotation.TwoGames;
		
		return g;
	}
	
	public static GameRules Tournament() {
		return Expert2004();
	}
}

package classes;

public class GameRules {
	
	public boolean activeIcons;
	public boolean allowStrategyCards;
	
	public int maxPlayers;
	public int reqStarters;
	public int lineupLength;
	
	// TODO: add support for 3+ way games
	// realistically, maybe use this to handle leagues?
	// public int teams;
	
	public String home_away;
	public String pitcher_rotation;
	public String al_nl;
	
	public boolean canSteal;
	public boolean simpleOuts;
	
	private static GameRules Default() {
		GameRules g = new GameRules();
		
		g.maxPlayers = 25;
		g.reqStarters = 4;
		g.lineupLength = 9;
		
		g.home_away = "random";
		g.pitcher_rotation = null;
		g.al_nl = "al";
		
		return g;
	}
	
	public static GameRules Basic2004() {
		GameRules g = Default();
		
		g.activeIcons = true;
		g.allowStrategyCards = false;
		g.canSteal = false;
		g.simpleOuts = true;
		
		return g;
	}
	
	public static GameRules Advanced2004() {
		GameRules g = Default();
		
		g.activeIcons = true;
		g.allowStrategyCards = false;
		g.canSteal = true;
		g.simpleOuts = false;
		
		return g;
	}
	
	public static GameRules Expert2004() {
		GameRules g = Default();
		
		g.activeIcons = false;
		g.allowStrategyCards = true;
		g.canSteal = true;
		g.simpleOuts = false;
		
		return g;
	}
	
	public static GameRules Tournament() {
		return Expert2004();
	}
}

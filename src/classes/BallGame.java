package classes;

public class BallGame {
	public Team home, away;
	public int inning;
	
	public int homescore, awayscore;
	public int outs;
	
	private Team batting;
	private Team fielding;
	
	public BallGame(Team h, Team a) {
		home = h;
		away = a;
		
		batting = away;
		fielding = home;
	}
	
	public void playInning() {
		outs = 0;
		
		do {
			playBatter();
		} while (outs < 3);
		
		// home bats last, so increment inning counter
		if(batting == home) {
			batting = away;
			fielding = home;
			inning++;
		}
		else {
			batting = home;
			fielding = away;
		}
	}
	
	public void playBatter() {
		boolean hit = false;
		
		do {
			hit = playPitch();
		} while(!hit);
	}
	
	public boolean playPitch() {
		
	}
}

package classes;

public class GameManager {
	public Team home, away;
	public int inning;
	
	public int homescore, awayscore;
	public int outs;
	
	private Team offense;
	private Team defense;
	
	public Batter first, second, third;
	
	public GameManager(Team h, Team a) {
		home = h;
		away = a;
		
		offense = away;
		defense = home;
	}
	
	public void playInning() {
		outs = 0;
		
		do {
			playAtBat();
			
			offense.atBat = (offense.atBat + 1) % offense.lineup.size(); // could do %9 this allows for nonstandard games
			
		} while (outs < 3);
		
		// home bats last, so increment inning counter
		if(offense == home) {
			offense = away;
			defense = home;
			inning++;
		}
		else {
			offense = home;
			defense = away;
		}
	}
	
	public void playAtBat() {
		// first, reset card status
		home.strategyAvailable = true;
		away.strategyAvailable = true;
		
		/* Stages, directly from rulebook
		 * 0. Steal base(s)
		 * 1. Offense may pinch hit
		 * 2. Defense may make pitching change
		 * 	2.5. Offense may pinch hit
		 * 3. Defense decides about intentional walk
		 * 4. Offense decides about sacrifice bunt
		 * 5. Take turns playing 'before the pitch' (or other) strategy cards
		 * 6. Roll for the pitch, roll for the swing (skip depending on 3/4)
		 * 7. Take turns playing "on result" cards
		 * 8. Carry out result
		 * 9. Take turns playing "after result" cards
		 */
		
		stealBases();
		
		offense.ai.determinePinchHit(this);
		defense.ai.determineReliever(this);
		offense.ai.determinePinchHit(this);
		
		defense.ai.forceWalk(this);
		offense.ai.forceBunt(this);
		
		offense.ai.resolveStrategyCards(5, this);
		defense.ai.resolveStrategyCards(5, this);
		
		playPitch();
		
		offense.ai.resolveStrategyCards(7, this);
		defense.ai.resolveStrategyCards(7, this);
			
	}
	
	public void stealBases() {
		/* Stages:
		 * -8. Determine which bases will attempt to steal
		 * -7. Play "before attempt" cards
		 * -6. Attempt steal(s). 1B->2B only if 1B+ previously, 2B->3B normal, 3B->HR has +5
		 * -5. Defense determines which to attempt to throw out
		 * -4. Roll dice
		 * -3. Play "on result" cards
		 * -2. Resolve results
		 * -1. Play "after result" cards
		 */
	}
	
	public boolean playPitch() {
		return false;
	}
}

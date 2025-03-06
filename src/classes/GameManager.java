package classes;

public class GameManager {
	public Team home, away;
	public int inning;
	
	public int homescore, awayscore;
	public int outs;
	
	private Team offense;
	private Team defense;
	
	public Batter first, second, third;
	
	public boolean hasFreeTagup;
	
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
		
		offense.pinchHit(this);
		defense.relievePitcher(this);
		offense.pinchHit(this);
		
		boolean forced_walk = defense.forceWalk(this);
		boolean forced_bunt = offense.forceBunt(this);
		
		playStrategies(5);
		
		if(forced_walk) {
			forcedWalk();
		}
		else if(forced_bunt) {
			bunt();
		}
		else {
			playPitch();
		}
		
		playStrategies(7);
			
	}
	
	public void stealBases() {
		/* Stages:
		 * -7. Determine which bases will attempt to steal
		 * -6. Play "before attempt" cards
		 * -5. Defense determines which to attempt to throw out
		 * -4. Roll dice
		 * -3. Play "on result" cards
		 * -2. Resolve results
		 * -1. Play "after result" cards
		 */
		
		int steal_num = offense.determineStealers(this);

		// if not steal 2nd or 3rd, short circuit the tagup
		if(steal_num % 2 == 0 && steal_num % 3 == 0) {
			if(!hasFreeTagup) {
				return;
			}
			
			if(second == null) {
				second = first;
				first = null;
			}
			
			return;
		}
		
		playStrategies(-7);
		
		int to_throw = defense.determineThrow(this, steal_num);
		boolean try_second = to_throw % 2 == 0, try_third = to_throw % 3 == 0;
		
		// roll dice
		int second_roll = -1, third_roll = -1;
		if(try_second && to_throw % 2 == 0) {
			// Contested
			second_roll = d20() + defense.outfielding();
		}
		if(try_third && to_throw % 3 == 0) {
			// Contested
			third_roll = d20() + defense.outfielding();
		}
		
		playStrategies(-3);
		
		// First calculating results; a runner being thrown out and bringing the total to 2 outs shouldn't
		// give the other runner a +5 bonus
		second_roll -= second.speed;
		second_roll -= (outs >= 2) ? 5 : 0; // additional +5 bonus for 2/3 outs
		
		third_roll -= third.speed + 5;	   // +5 bonus when stealing home
		third_roll -= (outs >= 2) ? 5 : 0; // additional +5 bonus for 2/3 outs
		
		// Now calculating outs; being thrown out at 2nd for the inning's 3rd out will prevent the home runner from scoring
		outs += (try_second && second_roll > 0) ? 1 : 0;
		outs += (try_third && third_roll > 0) ? 1 : 0;
		
		// resolve third first to clear space for person on 2nd
		third = null;
		if(try_third && third_roll < 0 && outs < 3) {
			score();
		}
		
		second = null;
		if(try_second && second_roll < 0) {
			third = second;
		}
		
		playStrategies(-1);
	}
	
	public boolean playPitch() {
		return false;
	}
	private void forcedWalk() {
		walk();
	}
	private void bunt() {
		
	}
	
	
	private void walk() {
		if(first != null) {
			if(second != null) {
				if(third != null) {
					score();
				}
				third = second;
			}
			second = first;
		}
		first = offense.lineup.get(offense.atBat);
	}
	private void advanceBases() {
		if(third != null) {
			score();
		}
		third = second;
		second = first;
	}
	private void advanceBases(int amt) {
		for(int i = 0; i < amt; i++) {
			advanceBases();
		}
	}
 	private void score() {
		score(1);
	}
	private void score(int runs) {
		if(offense == home) {
			homescore += runs;
		}
		else {
			awayscore += runs;
		}
	}
	
	private void playStrategies(int stage) {
		offense.playStrategyCard(this, stage);
		defense.playStrategyCard(this, stage);
	}
	
	private int d20() {
		return (int)(20 * Math.random() + 1);
	}
}

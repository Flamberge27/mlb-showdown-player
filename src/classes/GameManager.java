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
		 * 10. Steal bases
		 */
		
		offense.pinchHit(this);
		defense.relievePitcher(this);
		offense.pinchHit(this);
		
		boolean forced_walk = defense.forceWalk(this);
		boolean forced_bunt = offense.forceBunt(this);
		
		String hit_result = "";
		boolean allow_extra_bases = false;
		
		playStrategies(5);
		
		if(forced_walk) {
			hit_result = "FW";
		}
		else if(forced_bunt) {
			hit_result = "Bu";
		}
		else {
			hit_result = rollPitch();
		}
		
		playStrategies(7);
		
		allow_extra_bases = resolvePitch(hit_result);
		
		if(outs < 3 && allow_extra_bases) {
			stealBases();
		}
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
	
	public String rollPitch() {
		// keeping all of this very split up for debugging and strategy card purposes
		
		Pitcher pitching = defense.onMound;
		Batter batting = offense.lineup.get(offense.atBat);
		
		String[] chart;
		
		int roll = d20();
		
		if(batting.ob >= pitching.control + roll) {
			chart = batting.chart;
		}
		else {
			chart = pitching.chart;
		}
		
		roll = d20();
		
		String result = chart[roll];
		return result;
	}
	public boolean resolvePitch(String result) {
		Batter batting = offense.lineup.get(offense.atBat);
		
		// PU,SO,GB,FB,W,S,S+,DB,TR,HR
		switch(result) {
		/* Outs */
		case "PU":
			outs++;
			return false;
			
		case "SO":
			outs++;
			return false;
			
		case "GB":
			return groundBall();
			
		case "FB":
			outs++;
			return true; // can tag up after FB
		
		/* Special cases */
		case "B": // bunt case
			bunt();
			return false;
		case "FW": // forced walk
			walk();
			return false;
		
		/* Hits */
		case "W":
			walk();
			return false;
			
		case "S":
			advanceBases();
			
			first = batting;
			return true;
			
		case "S+":
			advanceBases();
			
			first = batting;
			hasFreeTagup = true;
			return true;
			
		case "DB":
			advanceBases();
			advanceBases();
			
			second = batting;
			return true;
			
		case "TR":
			advanceBases();
			advanceBases();
			advanceBases();
			
			third = batting;
			return true;
			
		case "HR":
			advanceBases();
			advanceBases();
			advanceBases();
			score();
			
			return false;
		}
		
		System.out.println("Debug - hit an impossible hit");
		return false;
	}
	
	private void bunt() {
		Pitcher pitching = defense.onMound;
		Batter batting = offense.lineup.get(offense.atBat);
		
		int roll = d20();
		
		String result = pitching.chart[roll];
		
		//bunter is always out
		outs++;
		
		if(result.equals("PU")) {
			return;
		}
		else {
			advanceBases();
		}
	}
	private boolean groundBall() {
		int roll;
		Batter batting = offense.lineup.get(offense.atBat);
		
		if(first == null) {
			outs++;
		}
		else {
			// player who was at first is always out
			outs++;
			
			// double play attempt
			roll = d20();
			if(defense.infielding() + roll > batting.speed) {
				outs++;
				first = null;
			}
			else {
				first = batting;
			}
		}
		
		//if outs >= 3, don't bother advancing bases
		if(outs >= 3) {
			return false;
		}
		
		// advance just 2nd and 3rd bases
		if(third != null) {
			score();
		}
		third = second;
		second = null;
		return false;
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

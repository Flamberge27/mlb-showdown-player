package classes;

public class GameManager {
	public Team home, away;
	public int inning;
	
	public int homescore, awayscore;
	public int outs;
	
	public Team offense;
	public Team defense;
	
	public Batter first, second, third;
	
	public boolean hasFreeTagup;
	public Outputter output;
	
	public GameManager(Team ho, Team aw) {
		home = ho;
		away = aw;
		
		offense = away;
		defense = home;
	}
	
	public void playGame() {
		inning = 1;
		
		while(inning <= 9) {
			playInning();
		}
	}
	
	public void playInning() {
		outs = 0;
		
		first = null;
		second = null;
		third = null;
		
		if(offense == away) {
			print("Top of inning #" + inning);
		}
		else {
			print("Switch sides! Bottom of inning #" + inning);
		}
		
		do {			
			playAtBat();
			
			offense.atBat = (offense.atBat + 1) % offense.lineup.size(); // could do %9 this allows for nonstandard games
			
		} while (outs < 3);
		
		// home bats last, so increment inning counter
		if(offense == home) {
			offense = away;
			defense = home;
			inning++;
			print("\nCurrent Score: " + 
					"\n   " + homescore + " (" + home.name + ")" + 
					"\n   " + awayscore + " (" + away.name + ")" +
					"\n-----");
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
		
		//print("  " + offense.lineup.get(offense.atBat) + " steps up to the plate!");
		
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
		
		//print("[" + outs + "/3] outs");
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
		if(steal_num % 2 != 0 && steal_num % 3 != 0) {
			if(!hasFreeTagup) {
				return;
			}
			
			if(second == null && first != null) {
				print("    + " + first + " tags up to 2nd");
				
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
		if (second != null) {
			second_roll -= second.speed;
			second_roll -= (outs >= 2) ? 5 : 0; // additional +5 bonus for 2/3 outs
		}
		
		if (third != null) {
			third_roll -= third.speed + 5;	   // +5 bonus when stealing home
			third_roll -= (outs >= 2) ? 5 : 0; // additional +5 bonus for 2/3 outs
		}
		
		// Now calculating outs; being thrown out at 2nd for the inning's 3rd out will prevent the home runner from scoring
		outs += (try_second && second_roll > 0) ? 1 : 0;
		outs += (try_third && third_roll > 0) ? 1 : 0;
		
		// resolve third first to clear space for person on 2nd
		if(try_third) {
			if(third_roll < 0) {
				if(outs < 3) {
					print("    * " + third + " steals home!");
					score();
					third.stats.SB++;
				}
				else {
					print("    - " + third + " steals home, but with 3 outs, the score doesn't count.");
				}
			} 
			else {
				print("    o " + third + " is thrown out stealing home");
				third.stats.CS++;
			}
		}
		third = null;
		
		if(try_second) {
			if(second_roll < 0) {
				print("    + " + second + " steals third!");
				third = second;
				third.stats.SB++;
			}
			else {
				print("    o " + second + " is thrown out stealing third");
				second.stats.CS++;
			}
		}
		second = null;
		
		playStrategies(-1);
	}
	
	public String rollPitch() {
		// keeping all of this very split up for debugging and strategy card purposes
		
		Pitcher pitching = defense.onMound;
		Batter batting = offense.lineup.get(offense.atBat);
		
		String[] chart;
		
		int roll = d20();
		
		if(batting.ob >= pitching.control + roll) {
			//print("  > Batter's advantage (" + pitching.control + " + {" + roll + "} vs. " + batting.ob + ")");
			chart = batting.chart;
		}
		else {
			//print("  > Pitcher's advantage (" + pitching.control + " + {" + roll + "} vs. " + batting.ob + ")");
			chart = pitching.chart;
		}
		
		roll = d20();
		
		String result = chart[roll];
		//print("    > Rolled a " + roll + ": " + result);
		return result;
	}
	public boolean resolvePitch(String result) {
		Pitcher pitching = defense.onMound;
		Batter batting = offense.lineup.get(offense.atBat);
		
		// PU,SO,GB,FB,W,S,S+,DB,TR,HR
		switch(result) {
		/* Outs */
		case "PU":
			print("  o " + batting + " pops it up.");
			outs++;
			return false;
			
		case "SO":
			print("  o " + batting + " strikes out.");
			outs++;
			batting.stats.SO++;
			pitching.stats.SO++;
			return false;
			
		case "GB":
			return groundBall();
			
		case "FB":
			print("  o " + batting + " hits a fly ball.");
			outs++;
			return true; // can tag up after FB
		
		/* Special cases */
		case "B": // bunt case
			bunt();
			return false;
		case "FW": // forced walk
			print("  + " + batting + " is forced to walk.");
			walk();
			
			batting.stats.BB++;
			pitching.stats.BB++;
			return false;
		
		/* Hits */
		case "W":
			print("  + " + batting + " gets a walk.");
			walk();
			
			batting.stats.BB++;
			pitching.stats.BB++;
			return false;
			
		case "S":
			print("  + " + batting + " hits a single.");
			advanceBases();
			first = batting;
			
			batting.stats.SG++;
			batting.stats.H++;
			pitching.stats.H++;
			return true;
			
		case "S+":
			print("  + " + batting + " hits a single.");
			advanceBases();
			first = batting;

			
			batting.stats.SG++;
			batting.stats.H++;
			pitching.stats.H++;
			hasFreeTagup = true;
			return true;
			
		case "DB":
			print("  + " + batting + " slams a double!");
			advanceBases();
			first = batting;
			
			advanceBases();
			
			batting.stats.DB++;
			batting.stats.H++;
			pitching.stats.H++;
			return true;
			
		case "TR":
			print("  + " + batting + " crushes a triple!");
			advanceBases();
			first = batting;
			
			advanceBases();
			advanceBases();

			batting.stats.TR++;
			batting.stats.H++;
			pitching.stats.H++;
			return true;
			
		case "HR":
			print("  + " + batting + " slams a home run!");
			advanceBases();
			first = batting;
			
			advanceBases();
			advanceBases();
			advanceBases();
			
			batting.stats.HR++;
			batting.stats.H++;
			pitching.stats.H++;
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
			print("  o " + batting + " does a bad bunt.");
			return;
		}
		else {
			print("  - " + batting + " finagles a good bunt.");
			advanceBases();
		}
	}
	private boolean groundBall() {
		int roll;
		Batter batting = offense.lineup.get(offense.atBat);
		
		if(first == null) {
			print("  o " + batting + " hits a grounder and is thrown out.");
			outs++;
		}
		else {
			// player who was at first is always out
			outs++;
			
			// double play attempt
			roll = d20();
			if(defense.infielding() + roll > batting.speed) {
				print("  oo " + batting + " hits a grounder. Double play!");
				outs++;
				first = null;
			}
			else {
				print("  o " + batting + " hits a grounder. " + first + " is thrown out trying for second.");
				first = batting;
			}
		}
		
		//if outs >= 3, don't bother advancing bases
		if(outs >= 3) {
			return false;
		}
		
		// advance just 2nd and 3rd bases
		if(third != null) {
			print("    * " + third + " scores!");
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
					
					offense.batting().stats.RBI++;
				}
				third = second;
			}
			second = first;
		}
		first = offense.lineup.get(offense.atBat);
	}
	private void advanceBases() {
		if(third != null) {
			print("    * " + third + " scores!");
			score();
			
			if(offense.batting() != third) {
				offense.batting().stats.RBI++;
			}
		}
		third = second;
		second = first;
		first = null;
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
		
		third.stats.R++;
		defense.onMound.logRun();
	}
	
	private void playStrategies(int stage) {
		offense.playStrategyCard(this, stage);
		defense.playStrategyCard(this, stage);
	}
	
	private int d20() {
		return (int)(20 * Math.random() + 1);
	}

	public void print(String s) {
		if(output != null) {
			output.output(s);
		}
	}
}

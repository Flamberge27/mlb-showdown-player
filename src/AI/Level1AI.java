package AI;

import java.util.ArrayList;

import classes.*;

public class Level1AI extends BarebonesAI {
	public void AssignBases(Team t) {
		/*general principles:
		 * 1. High on base, high speed, low out, hitting is less important
		 * 2. Highest on base, low out, speed if possible
		 * 3. High on base, good hitter
		 * 4. Best hitter
		 * 5. Next best hitter
		 * 6. fill
		 * 7. fill
		 * 8. High speed
		 * 9. Highest speed, low on base
		 */
		
		Batter[] orderedLineup = new Batter[9];
		int[] priorities = new int[] {3, 1, 2, 0, 8, 7, 4, 5, 6}; // the order in which to fill slots
		
		Batter candidate;
		int pos;
		double thisscore, curscore = 0; // here as intermediate variables for debugging
		
		for(int i = 0; i < 9; i++) { // going through priority list
			candidate = null;
			
			pos = priorities[i];
			
			for(Batter b: t.lineup) {
				thisscore = posscore(b, pos);
				if(candidate == null ||  thisscore > curscore) {
					candidate = b;
					curscore = thisscore;
				}
			}
			
			orderedLineup[pos] = candidate;
			t.lineup.remove(candidate);
		}
		
		ArrayList<Batter> newlineup = new ArrayList<>();
		for(int i = 0; i < 9; i++) {
			newlineup.add(orderedLineup[i]);
		}
		t.lineup = newlineup;
	}

	protected double score(Batter b, double obf, double spf, double outf, double hitf) {
		double base = 0;
		
		base += obf * (b.ob - 12) ;
		base += (b.speed >= 20) ? spf * (b.speed - 15) : 0;
		base += outf * b.outage();
		
		// add minor penalty for hitting well, mostly for breaking ties
		for(int i = 1; i <= 20; i++) {
			switch(b.chart[i]) {
			case "DB":
				base += 1.0 * hitf;
				break;
			case "TR":
				base += 1.5 * hitf;
				break;
			case "HR":
				base += 2.0 * hitf;
				break;
			}
		}
		
		return base;
	}
	
	protected double posscore(Batter b, int pos) {
		switch(pos) {
		case 0:
			return score(b, 3, 1, -1, -0.25);
		case 1:
			return score(b, 5, 1, -1, -0.25);
		case 2:
			return score(b, 3, 0, -1, 2);
		case 3:
			return score(b, 3, 0, -1, 3);
		case 4:
			return score(b, 2, 0, -1, 2);
		case 5:
			return score(b, 1, 0, -1, 1);
		case 6:
			return score(b, 1, 0, -1, 0);
		case 7:
			return score(b, 1, 1, -1, 0);
		case 8:
			return score(b, 0, 2, -1, 0);
		}
		System.out.println("Attempted to get a positional score for invalid position: " + pos);
		return 0.0;
	}
	
	@Override
	public int determineStealers(GameManager g) {
		// return 0 to represent no bases to steal
		int toSteal = 1;
		
		Batter on2 = g.second;
		Batter on3 = g.third;
		
		double chance2 = 0.0, chance3 = 0.0;
		
		if(on3 != null) {
			chance3 = (on3.speed + 5 + ((g.outs >= 2) ? 5 : 0) - g.defense.outfielding()) / 20.0;
			
			if(chance3 >= 0.75) {
				toSteal *= 3;
			}
		}
		
		if(on2 != null) {
			chance2 = (on2.speed - g.defense.outfielding()) / 20.0;
			
			if(chance2 >= 0.75) {
				toSteal *= 2;
			}
			else if (chance2 >= 0.60 && toSteal % 3 == 0 && g.outs < 2) {
				// if we can trade an out for a run by drawing a throw to 2nd, do so
				// obviously don't do this if it would cause the 3rd out, thus invalidating the run
				toSteal *= 2;
			}
		}
		
		return toSteal;
	}
	
	@Override
	public int determineThrow(GameManager g, int steal_num) {
		if(steal_num == 2) {
			return 2;
		}
		if(steal_num == 3) {
			return 3;
		}
		if(steal_num == 6) {
			Batter on2 = g.second;
			Batter on3 = g.third;
			double chance2 = (on2.speed - g.defense.outfielding()) / 20.0;
			double chance3 = (on3.speed + 5 + ((g.outs >= 2) ? 5 : 0) - g.defense.outfielding()) / 20.0;
			
			if(chance3 >= 1) {
				return 2;
			}
			if(chance2 >= 1) {
				return 3;
			}
			
			if(g.outs >= 2) {
				return (chance2 < chance3) ? 2 : 3;
			}
			
			return 3;
		}
		
		return 0;
	}
	
	@Override
	public void determineReliever(GameManager g) {
		/* One of three requirements has to be met:
		 * 	After the 4th inning
		 *  Has given up 10 or more runs
		 *  Strategy card has forced him out of game
		 */
		Pitcher pitching = team.onMound;
		if(g.inning <= 4 && pitching.runsAllowed() < 10 && pitching.canPlay) {
			return;
		}
		
		// Now determine a reliever
		// Basic idea - do a rough calc of how many innings of relievers
		// you have that are better than the current guy
		// If you can fill out through the 9th, might as well sub
		ArrayList<Pitcher> relievers = new ArrayList<Pitcher>();
		int total_innings = 0, highest_pos = 0;
		for(Pitcher p : team.bullpen) {
			if(p == pitching) {
				continue;
			}
			if(p.position == Position.Starter) {
				continue; // Can't sub in a starter
			}
			if(!p.canPlay) {
				continue;
			}
			
			// now add to the list of possible relievers
			if(naiveQuality(p) >= naiveQuality(pitching)) {
				total_innings += p.ip;
				total_innings += naiveQuality(p) - naiveQuality(pitching);
				
				relievers.add(p);
				if(naiveQuality(p) > naiveQuality(relievers.get(highest_pos))) {
					highest_pos = relievers.size()-1;
				}
			}
		}
		
		if(g.inning + total_innings < 9 || relievers.size() < 1) {
			// we don't have enough gas left; don't bother
			return;
		}
		
		//do the switch
		g.print("  <> " + pitching + " is relieved by " + relievers.get(highest_pos));
		pitching.canPlay = false;
		team.onMound = relievers.get(highest_pos);
	}
	
	private static int naiveQuality(Pitcher p) {
		return p.outage() + p.control;
	}
}

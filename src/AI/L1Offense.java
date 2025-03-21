package AI;

import java.util.ArrayList;

import classes.Batter;
import classes.GameManager;
import classes.Team;

public class L1Offense implements OffenseAI {
	
	private Team team;
	
	@Override
	public void setTeam(Team t) {
		team = t;
	}

	// TODO: tweaks, maybe
	@Override
	public void ChooseStartingLineup() {
		for(int i = 0; i < team.roster.size(); i++) {
			team.subs.add(team.roster.get(i));
		}
		
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
			
			for(Batter b: team.subs) {
				thisscore = posscore(b, pos);
				if(candidate == null ||  thisscore > curscore) {
					candidate = b;
					curscore = thisscore;
				}
			}
			
			orderedLineup[pos] = candidate;
			team.subs.remove(candidate);
		}
		
		team.lineup = new ArrayList<>();
		for(int i = 0; i < 9; i++) {
			team.lineup.add(orderedLineup[i]);
		}
	}

	@Override
	public void determinePinchHit(GameManager g) {
		// Do not pinch hit at Level 1
	}

	@Override
	public boolean forceBunt(GameManager g) {
		// Do not bunt at level 1
		return false;
	}
	
	// TODO: tweaks
	@Override
	public int determineStealers(GameManager g) {
		// return 0 or 1 to represent no bases to steal
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
	public boolean resolveStrategyCards(GameManager g, int phase) {
		// Do not use strategy cards at level 1
		return false;
	}
	
	// Returns the player's positional score; has pre-loaded weights, which are then sent into score()
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

	// Determines a player's score based on given weights (obf = on-base factor, etc.)
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
	
}

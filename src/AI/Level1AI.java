package AI;

import java.util.ArrayList;

import classes.Batter;
import classes.Team;

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
}

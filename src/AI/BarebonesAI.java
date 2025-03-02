package AI;

import java.util.ArrayList;

import classes.*;

public class BarebonesAI implements AI {

	@Override
	public void ChooseStartingPitcher(Team t) {
		// Simply get the highest point starter
 		int currentPick = -1, currentPoints = 0;
		
		for(int i = 0; i < t.bullpen.size(); i++) {
			Pitcher p = t.bullpen.get(i);
			if(p.position.equals("Starter") && (currentPick == -1 || p.points > currentPoints)) {
				currentPick = i;
				currentPoints = p.points;
			}
		}
		
		t.onMound = t.bullpen.get(currentPick);
	}

	@Override
	public void ChooseStartingLineup(Team t) {
		// Pick the first 9 players
		for(int i = 0; i < 9; i++) {
			t.lineup.add(t.roster.get(i));
		}
		for(int i = 9; i < t.roster.size(); i++) {
			t.subs.add(t.roster.get(i));
		}
	}

	@Override
	public void AssignBases(Team t) {
		// The most possible barebones algorithm - assume lineups are read-in in the expected order
		t.at_catcher = t.lineup.get(0);
		t.at_first = t.lineup.get(1);
		t.at_second = t.lineup.get(2);
		t.at_short = t.lineup.get(3);
		t.at_third = t.lineup.get(4);
		t.at_left = t.lineup.get(5);
		t.at_center = t.lineup.get(6);
		t.at_right = t.lineup.get(7);
		t.DH = t.lineup.get(8);
	}

}

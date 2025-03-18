package AI;

import java.util.ArrayList;

import classes.*;

public class BarebonesAI implements OffenseAI, DefenseAI {
	
	public Team team;
	
	@Override
	public void setTeam(Team t) {
		team = t;
	}

	@Override
	public void ChooseStartingPitcher() {
		// Simply get the highest point starter
 		int currentPick = -1, currentPoints = 0;
		
		for(int i = 0; i < team.bullpen.size(); i++) {
			Pitcher p = team.bullpen.get(i);
			if(p.position == Position.Starter && (currentPick == -1 || p.points > currentPoints)) {
				currentPick = i;
				currentPoints = p.points;
			}
		}
		
		team.onMound = team.bullpen.get(currentPick);
	}

	@Override
	public void ChooseStartingLineup() {
		// Pick the first 9 players
		for(int i = 0; i < 9; i++) {
			team.lineup.add(team.roster.get(i));
		}
		for(int i = 9; i < team.roster.size(); i++) {
			team.subs.add(team.roster.get(i));
		}
	}

	@Override
	public void AssignBases() {
		// The most possible barebones algorithm - assume lineups are read-in in the expected order
		team.at_catcher = team.lineup.get(0);
		team.at_first = team.lineup.get(1);
		team.at_second = team.lineup.get(2);
		team.at_short = team.lineup.get(3);
		team.at_third = team.lineup.get(4);
		team.at_left = team.lineup.get(5);
		team.at_center = team.lineup.get(6);
		team.at_right = team.lineup.get(7);
		team.DH = team.lineup.get(8);
	}

	@Override
	public void determinePinchHit(GameManager g) {
		// Do not pinch hit
	}

	@Override
	public void determineReliever(GameManager g) {
		// Do not swap in a reliever
	}

	@Override
	public boolean forceWalk(GameManager g) {
		// Do not force a walk
		return false;
	}

	@Override
	public boolean forceBunt(GameManager g) {
		// Do not force a bunt
		return false;
	}

	@Override
	public boolean resolveStrategyCards(GameManager g, int phase) {
		// Don't interact with strategy cards
		return false;
	}

	@Override
	public int determineStealers(GameManager g) {
		// return 0 to represent no bases to steal
		return 1;
	}
	
	@Override
	public int determineThrow(GameManager g, int steal_num) {
		// for now, do the simple thing
		if(steal_num % 2 == 0) {
			return 2;
		}
		if(steal_num % 3 == 0) {
			return 3;
		}
		
		return 0;
	}

}

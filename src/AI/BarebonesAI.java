package AI;

import classes.GameManager;
import classes.Pitcher;
import classes.Position;
import classes.Team;

public class BarebonesAI implements OffenseAI, DefenseAI {
	
	public Team team;
	
	@Override
	public void setTeam(Team t) {
		team = t;
	}

	@Override
	public void ChooseStartingPitcher() {
		// Simply get the highest point starter
		Pitcher currentPick = null;
		
		
		for(Pitcher p : team.starters) {
			if(currentPick == null || p.points > currentPick.points) {
				currentPick = p;
			}
		}
		
		team.onMound = currentPick;	
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
		team.setFielder(Position.Catcher, team.lineup.get(0));
		team.setFielder(Position.First, team.lineup.get(1));
		team.setFielder(Position.Second, team.lineup.get(2));
		team.setFielder(Position.Short, team.lineup.get(3));
		team.setFielder(Position.Third, team.lineup.get(4));
		team.setFielder(Position.Left, team.lineup.get(5));
		team.setFielder(Position.Center, team.lineup.get(6));
		team.setFielder(Position.Right, team.lineup.get(7));
		//team.setFielder(Position.None, team.lineup.get(8)); for DH
	}

	@Override
	public void determinePinchHit(GameManager g) {
		// Do not pinch hit
	}

	@Override
	public Pitcher determineReliever(GameManager g) {
		// Do not swap in a reliever
		return null;
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

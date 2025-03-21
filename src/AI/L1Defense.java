package AI;

import java.util.ArrayList;

import classes.Batter;
import classes.GameManager;
import classes.Pitcher;
import classes.Position;
import classes.Team;

public class L1Defense implements DefenseAI {
	
	private Team team;
	
	@Override
	public void setTeam(Team t) {
		// TODO Auto-generated method stub
		team = t;
	}
	
	// TODO: tweaks
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
	
	// TODO
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
			if(naivePitcherQuality(p) >= naivePitcherQuality(pitching)) {
				total_innings += p.ip;
				total_innings += naivePitcherQuality(p) - naivePitcherQuality(pitching);
				
				relievers.add(p);
				if(naivePitcherQuality(p) > naivePitcherQuality(relievers.get(highest_pos))) {
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

	@Override
	public boolean forceWalk(GameManager g) {
		// Do not force walks at level 1
		return false;
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
	public boolean resolveStrategyCards(GameManager g, int phase) {
		// Do not use strategy cards at level 1
		return false;
	}

	private static int naivePitcherQuality(Pitcher p) {
		return p.outage() + p.control;
	}
	
}

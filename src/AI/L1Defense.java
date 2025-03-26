package AI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
	
	@Override
	public void AssignBases() {
		// There are definitely ways to not explicitly write this out, but I'm fine with it being explicit like this
		List<Position> positions = Arrays.asList(Position.Catcher, Position.First, Position.Second, Position.Short,
												Position.Third, Position.Left, Position.Center, Position.Right);
		
		// And guess what, it's an assignment problem! Luckily, we have algorithms for that
		int[][] matrix = new int[9][8];
		int max = -1, f;
		for(int b = 0; b < 9; b++) {
			for(int p = 0; p < 8; p++) {
				f = team.lineup.get(b).fielding(positions.get(p)) + 3;
				matrix[b][p] = f; // turn -1s to 0s
				
				f = Math.max(max,  f);
			}
		}
		
		int[] order = mismarAssignment(matrix);
		
		for(int p = 0; p < 8; p++) {
			team.setFielder(positions.get(p), team.lineup.get(order[p]));
		}
		
		// System.out.println("Debug pause");
		
	}
	
	
	// See https://www.iosrjournals.org/iosr-jm/papers/Vol16-issue1/Series-3/E1601032934.pdf
	private static int[] mismarAssignment(int[][] matrix) {
		int rows = matrix.length, cols = matrix[0].length;
		
		int[] row_sums = new int[rows];
		int[] col_sums = new int[cols];
		int total_sum = 0;
		
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				row_sums[r] += matrix[r][c];
				col_sums[c] += matrix[r][c];
				total_sum += matrix[r][c];
			}
		}
		
		double[][] expected = new double[rows][cols];
		double[][] deltas = new double[rows][cols];
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				expected[r][c] = row_sums[r] * col_sums[c] * 1.0 / total_sum;
				deltas[r][c] = matrix[r][c] - expected[r][c];
			}
		}
		
		int[] assignments = new int[cols];
		int maxr, maxc;
		ArrayList<Integer> assigned_rows = new ArrayList<Integer>();
		ArrayList<Integer> assigned_cols = new ArrayList<Integer>();
		do {
			maxr = -1;
			maxc = -1;
			
			//find max
			for(int r = 0; r < rows; r++) {
				// skip previously used rows
				if(assigned_rows.contains(r)) {
					continue;
				}
				
				for(int c = 0; c < cols; c++) {
					// skip previous used columns
					if(assigned_cols.contains(c)) {
						continue;
					}
					
					if(maxr == -1 || deltas[maxr][maxc] < deltas[r][c]) {
						maxr = r;
						maxc = c;
					}
				}
			}
			
			assigned_rows.add(maxr);
			assigned_cols.add(maxc);
			assignments[maxc] = maxr;
		} 
		while (assigned_rows.size() < assignments.length);
		
		return assignments;
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

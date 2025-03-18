package AI;

import classes.GameManager;
import classes.Team;

public interface DefenseAI {
	
	public void setTeam(Team t);
	
	public void ChooseStartingPitcher(); // Determine which pitcher is starting
	public void AssignBases();  // Assign bases for a team
	
	public void determineReliever(GameManager g);
	public boolean forceWalk(GameManager g);
	public int determineThrow(GameManager g, int steal_num);
	
	public boolean resolveStrategyCards(GameManager g, int phase);
}

package AI;

import classes.*;

public abstract class AI {
	public Team team;
	
	public abstract void ChooseStartingPitcher(); // Determine which pitcher is starting
	public abstract void ChooseStartingLineup(); // Determine starting lineup
	
	public abstract void AssignBases();  // Assign bases for a team
	
	public abstract void determinePinchHit(GameManager g); // 
	public abstract void determineReliever(GameManager g);
	
	public abstract boolean forceWalk(GameManager g);
	public abstract boolean forceBunt(GameManager g);
	
	public abstract boolean resolveStrategyCards(GameManager g, int phase);
	
	public abstract int determineStealers(GameManager g);
	public abstract int determineThrow(GameManager g, int steal_num);
}

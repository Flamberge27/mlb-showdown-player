package AI;

import classes.*;

public abstract class AI {
	public Team myTeam;
	
	public abstract void ChooseStartingPitcher(); // Determine which pitcher is starting
	public abstract void ChooseStartingLineup(); // Determine starting lineup
	
	public abstract void AssignBases();  // Assign bases for a team
	
	public abstract void determinePinchHit(GameManager g); // 
	public abstract void determineReliever(GameManager g);
	
	public abstract boolean forceWalk(GameManager g);
	public abstract boolean forceBunt(GameManager g);
	
	public abstract boolean resolveStrategyCards(int phase, GameManager g);
	
	public abstract int trySteal(GameManager g);
}

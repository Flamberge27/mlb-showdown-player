package AI;

import classes.GameManager;
import classes.Team;

public interface OffenseAI {
	
	public void setTeam(Team t);
	
	public void ChooseStartingLineup(); // Determine starting lineup
	
	public void determinePinchHit(GameManager g);
	public boolean forceBunt(GameManager g);
	public int determineStealers(GameManager g);
	
	public boolean resolveStrategyCards(GameManager g, int phase);
	
}

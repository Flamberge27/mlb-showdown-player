package AI;

import classes.*;

public interface AI {
	public void ChooseStartingPitcher(Team t); // Determine which pitcher is starting
	public void ChooseStartingLineup(Team t); // Determine starting lineup
	public void AssignBases(Team t); // Assign bases for a team
}

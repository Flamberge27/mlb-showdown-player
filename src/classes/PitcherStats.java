package classes;

public class PitcherStats {
	public int G; // Games Pitched
	public int GS; // Games Started
	public int GF; // Games Finished
	public int CG; // Complete Games
	
	public int SHO; // Shutouts
	public double IP; // Innings Pitched
	
	public int H; // Hits allowed
	public int ER; // Earned Runs allowed
	
	public int HR; // Home Runs Allowed
	public int BB; // Walks
	public int SO; // Strikeouts
	
	public int BF; // Batters Faced
	
	public PitcherStats() {
		G = 0;
		GS = 0;
		GF = 0;
		CG = 0;
		SHO = 0;
		IP = 0.0;
		H = 0;
		ER = 0;
		HR = 0;
		BB = 0;
		SO = 0;
		BF = 0;
	}
	
	public double ERA() {
		return ER / IP;
	}
	
	public double FIP() {
		return (13 * HR + 3 * BB - 2 * SO) / IP;
	}
	
	public double WHIP() {
		return (BB + H) / IP;
	}
}

package classes;

public class Pitcher extends Player
{
	public int control;
	public int ip;
	public String position;
	
	private int innings_pitched;
	private int runs_allowed;
	
	public PitcherStats stats;
	
	public Pitcher()
	{
		super();
	}
	
	public Pitcher(String[] stats) {
		super(stats);
	}
	
	public Pitcher(String[] stats, int[] positionMap) {
		super(stats, positionMap);
		
		this.control = Integer.parseInt(stats[positionMap[8]]);
		this.ip = Integer.parseInt(stats[positionMap[9]]);
		this.position = stats[positionMap[10]];
	}
	
	@Override
	public void gameReset() {
		this.innings_pitched = 0;
		this.runs_allowed = 0;
		
		if(this.stats == null) {
			this.stats = new PitcherStats();
		}
		this.stats.G++;
	}
	public void fullReset() {
		this.innings_pitched = 0;
		this.runs_allowed = 0;
		
		this.stats = new PitcherStats();
	}
	
	public int current_control() {
		int con = control;
		
		if(innings_pitched > ip) {
			con -= (innings_pitched - ip);
		}
		
		if(runs_allowed >= 3) {
			con -= runs_allowed / 3; // integer division to cut off remainder
		}
		
		return con;
	}
	
	public void logRun() {
		this.runs_allowed++;
		this.stats.ER++;
	}
	public void logInning() {
		this.innings_pitched++;
		this.stats.IP++;
	}
	public void logInning(int my_batters, int total_batters) {
		this.innings_pitched++;
		this.stats.IP += my_batters * 1.0 / total_batters;
	}
}

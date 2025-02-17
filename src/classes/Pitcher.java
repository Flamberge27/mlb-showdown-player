package classes;

public class Pitcher extends Player
{
	public int control;
	public int ip;
	public String position;
	
	public int remaining_ip;
	
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
	
	
}

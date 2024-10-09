package classes;

public class Hitter extends Player
{
	public int ob;
	public int speed;
	public Fielding fielding;
	
	public Hitter()
	{
		super();
	}
	
	public Hitter(String[] stats) {
		super(stats);
	}
	
	public Hitter(String[] stats, int[] positionMap) {
		super(stats, positionMap);
		
		
	}
}

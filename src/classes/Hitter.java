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
		
		this.ob = Integer.parseInt(stats[positionMap[6]]);
		this.speed = parseSpeed(stats[positionMap[7]]);
		this.fielding = new Fielding(stats[positionMap[8]]);
	}
	
	public int parseSpeed(String speed) {
		// cards from 00/01 have speed listed as A/B/C for 20/15/10
		// this is a quick way to check if our speed is a character
		if(speed.charAt(0) > 64) {
			// A is ascii 65: return 20 - 5*0 = 20
			// B is ascii 66: return 20 - 5*1 = 15
			// C is ascii 67: return 20 - 5*2 = 10
			return 20 - 5*(speed.charAt(0) - 65);
		}
		
		else {
			return Integer.parseInt(speed);
		}
	}
}

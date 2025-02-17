package classes;

public class Batter extends Player
{
	public int ob;
	public int speed;
	public Fielding fielding;
	
	public Batter()
	{
		super();
	}
	
	public Batter(String[] stats) {
		this(stats, new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22});
	}
	
	public Batter(String[] stats, int[] positionMap) {
		super(stats, positionMap);
		
		this.ob = Integer.parseInt(stats[positionMap[8]]);
		this.speed = parseSpeed(stats[positionMap[9]]);
		this.fielding = new Fielding(stats[positionMap[10]]);
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

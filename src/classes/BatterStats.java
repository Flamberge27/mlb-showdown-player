package classes;

public class BatterStats {
	public int G;
	public int AB;
	
	public int R;
	public int H;
	
	public int BB;
	public int SG; // singles
	public int DB; // doubles
	public int TR; // triples
	public int HR; // home runs
	public int RBI;  // RBIs
	
	public int SB;
	public int CS;
	
	public int SO;
	
	public BatterStats() {
		G = 0;
		AB = 0;
		R = 0;
		H = 0;
		BB = 0;
		DB = 0;
		TR = 0;
		HR = 0;
		RBI = 0;
		SB = 0;
		CS = 0;
		SO = 0;
	}
	
	public double BA() {
		return H * 1.0 / AB;
	}
	public double OBP() {
		return (H + BB) * 1.0 / AB;
	}
	public int TB() {
		return (SG + 2 * DB + 3 * TR + 4 * HR);
	}
	public double SLG() {
		return (1.0 * TB()) / AB;
	}
	public double OPS() {
		return OBP() + SLG();
	}
}

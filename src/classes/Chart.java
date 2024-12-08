package classes;

import console_version.DataParser;

public class Chart {
	String[] results;
	
	public Chart(String[] stats, int[] positionMap) {
		results = new String[30];
		
		for(int i = 13; i < 23; i++) {
			addResult(stats[positionMap[i]], DataParser.mapInt(i));
		}
	}
	
	public void addResult(String rawRange, String pos) {
		/* A couple different possibilities here:
		 
		   -	(do nothing)
		   #	(just #, # may be two digits)
		   #-	(just #, # may be two digits)
		   #-#	(all numbers in the given range, inclusive)
		   #+	(# and all future numbers
		 */
		
		// first case, just a dash means do nothing
		if(rawRange.equals("-"))
			return;
		
		// condensed all the other cases because I could
		int start_at, end_at;
		if(rawRange.contains("-")) {
			
			// second or third case
			String[] split = rawRange.split("-");
			start_at = Integer.parseInt(split[0]);
			end_at = (split.length == 1) ? start_at : Integer.parseInt(split[1]);
			
		}
		else if(rawRange.indexOf('+') == -1) {
			start_at = Integer.parseInt(rawRange);
			end_at = start_at;
		}
		else {
		
			// assume input is valid, if it isn't we'll crash
			// split to the + in case there are any /n chars (or similar) lying around
			int plus_pos = rawRange.indexOf('+');
			start_at = Integer.parseInt(rawRange.substring(0, plus_pos));
			end_at = results.length - 1;
		}
		
		for(int change_pos = start_at; change_pos <= end_at; change_pos++) {
			results[change_pos] = pos;
		}
	}
}
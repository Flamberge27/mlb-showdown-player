package data;

import java.util.ArrayList;

public abstract class CustomFunctions {
	// See https://www.iosrjournals.org/iosr-jm/papers/Vol16-issue1/Series-3/E1601032934.pdf
	public static int[] mismarAssignment(int[][] matrix) {
		int rows = matrix.length, cols = matrix[0].length;
		
		int[] row_sums = new int[rows];
		int[] col_sums = new int[cols];
		int total_sum = 0;
		
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				row_sums[r] += matrix[r][c];
				col_sums[c] += matrix[r][c];
				total_sum += matrix[r][c];
			}
		}
		
		double[][] expected = new double[rows][cols];
		double[][] deltas = new double[rows][cols];
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				expected[r][c] = row_sums[r] * col_sums[c] * 1.0 / total_sum;
				deltas[r][c] = matrix[r][c] - expected[r][c];
			}
		}
		
		int[] assignments = new int[cols];
		int maxr, maxc;
		ArrayList<Integer> assigned_rows = new ArrayList<Integer>();
		ArrayList<Integer> assigned_cols = new ArrayList<Integer>();
		do {
			maxr = -1;
			maxc = -1;
			
			//find max
			for(int r = 0; r < rows; r++) {
				// skip previously used rows
				if(assigned_rows.contains(r)) {
					continue;
				}
				
				for(int c = 0; c < cols; c++) {
					// skip previous used columns
					if(assigned_cols.contains(c)) {
						continue;
					}
					
					if(maxr == -1 || deltas[maxr][maxc] < deltas[r][c]) {
						maxr = r;
						maxc = c;
					}
				}
			}
			
			assigned_rows.add(maxr);
			assigned_cols.add(maxc);
			assignments[maxc] = maxr;
		} 
		while (assigned_rows.size() < assignments.length);
		
		return assignments;
	}

	
}

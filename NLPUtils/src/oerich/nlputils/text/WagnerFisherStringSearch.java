package oerich.nlputils.text;

/**
 * Nach dem Wagner-Fischer Verfahren da Abstand 1 sein soll, wurde hier etwas
 * optimiert, um mehr Performance zu haben
 */
public class WagnerFisherStringSearch implements IApproximativeStringSearch {

	@Override
	public int getDistance(String a, String b) {

		if (a == null && b == null) {
			return 0;
		}

		if (a == null) {
			return b.length();
		}

		if (b == null) {
			return a.length();
		}

		int m = a.length();
		int n = b.length();

		int[][] d = new int[m + 1][n + 1];
		d[0][0] = 0;
		for (int i = 1; i < m + 1; i++) {
			d[i][0] = i;
			// d[i][0] = d[i-1][0] + weight(x.charAt(i-1), '\u03B5');
		}
		for (int j = 1; j < n + 1; j++) {
			d[0][j] = j;
			// d[0][j] = d[0][j-1] + weight(y.charAt(j-1), '\u03B5');
		}

		for (int i = 1; i < m + 1; i++) {
			for (int j = 1; j < n + 1; j++) {
				d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1),
						d[i - 1][j - 1]
								+ (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1));
				// d[i][j] = Math.min(Math.min(d[i-1][j] + 1, d[i][j-1] + 1),
				// d[i-1][j-1] + weight(x.charAt(i-1), y.charAt(j-1)));
				// d[i][j] = Math.min( d[i-1][j] + weight(x.charAt(i-1),
				// '\u03B5'),
				// Math.min(d[i][j-1] + weight(y.charAt(j-1), '\u03B5'),
				// d[i-1][j-1] + weight(x.charAt(i-1), y.charAt(j-1))));
			}
		}

		return d[m][n];

	}
}

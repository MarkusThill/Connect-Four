package miscellaneous;

import c4.ConnectFour;

/**
 * With this little class it is possible to calculate the
 *         number of four-rows which can be put through every single field.
 * @author Markus Thill 
 */
public class NumFourRows extends ConnectFour {

	/**
	 * All Four-Rows with exception of the vertical ones
	 */
	long fourRowsVert[] = { 0x1041040000L, 0x41041000L, 0x1041040L, 0x41041L,
			0x2082080000L, 0x82082000L, 0x2082080L, 0x82082L, 0x1084200000L,
			0x42108000L, 0x1084200L, 0x42108L, 0x8102040000L, 0x204081000L,
			0x8102040L, 0x204081L, 0x4104100000L, 0x104104000L, 0x4104100L,
			0x104104L, 0x2108400000L, 0x84210000L, 0x2108400L, 0x84210L,
			0x10204080000L, 0x408102000L, 0x10204080L, 0x408102L,
			0x8208200000L, 0x208208000L, 0x8208200L, 0x208208L, 0x4210800000L,
			0x108420000L, 0x4210800L, 0x108420L, 0x20408100000L, 0x810204000L,
			0x20408100L, 0x810204L, 0x10410400000L, 0x410410000L, 0x10410400L,
			0x410410L, 0x20820800000L, 0x820820000L, 0x20820800L, 0x820820L };
	
	NumFourRows() {
		super();
	}

	/**
	 * @param vert true, if vertical rows shall be used
	 * @return array containing the number of four-rows for every cell
	 */
	public int[][] calculate(boolean vert) {
		long[] fr = (vert? fourRows : fourRowsVert);
		int[][] numFourRows = new int[COLCOUNT][ROWCOUNT];
		for (int i = 0; i < COLCOUNT; i++)
			for (int j = 0; j < ROWCOUNT; j++)
				for (int k = 0; k < fr.length; k++)
					if ((fr[k] & getMask(i, j)) != 0L)
						numFourRows[i][j]++;
		return numFourRows;
	}

	public static void main(String[] args) {
		NumFourRows nfr = new NumFourRows();
		int[][] res = nfr.calculate(false);

		// Print
		System.out.println("Number of four-rows which can be put "
				+ "through every single field of the board:\n");
		for (int j = res[0].length - 1; j >= 0; j--) {
			for (int i = 0; i < res.length; i++)
				System.out.print(res[i][j] + "\t");
			System.out.println("");
		}
	}
}

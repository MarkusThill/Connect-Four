package miscellaneous;

import c4.ConnectFour;

/**
 * 
 * The method findThreat(the source) was generated with
 *         this program, because its source-code is very long.It can be chosen
 *         if findThreats or findOddThreats is generated <br>
 * 
 * @author Markus Thill 
 * 
 */
public class GenerateSource extends ConnectFour {

	/**
	 * All Four-Rows with exception of the vertical ones
	 */
	long fourRows[] = { 0x1041040000L, 0x41041000L, 0x1041040L, 0x41041L,
			0x2082080000L, 0x82082000L, 0x2082080L, 0x82082L, 0x1084200000L,
			0x42108000L, 0x1084200L, 0x42108L, 0x8102040000L, 0x204081000L,
			0x8102040L, 0x204081L, 0x4104100000L, 0x104104000L, 0x4104100L,
			0x104104L, 0x2108400000L, 0x84210000L, 0x2108400L, 0x84210L,
			0x10204080000L, 0x408102000L, 0x10204080L, 0x408102L,
			0x8208200000L, 0x208208000L, 0x8208200L, 0x208208L, 0x4210800000L,
			0x108420000L, 0x4210800L, 0x108420L, 0x20408100000L, 0x810204000L,
			0x20408100L, 0x810204L, 0x10410400000L, 0x410410000L, 0x10410400L,
			0x410410L, 0x20820800000L, 0x820820000L, 0x20820800L, 0x820820L };

	private class ReturnValue {
		int col1 = -1, col2 = -1, row1 = -1, row2 = -1;
	}

	public static void main(String[] args) {
		GenerateSource gs = new GenerateSource();
		gs.createMethod(false);
	}

	/**
	 * Generate the source-Code for the Method findThreats. It can be chosen if
	 * findThreats or findOddThreats is generated
	 */
	void createMethod(boolean oddThreats) {
		int i, j, k, l, m;
		long array[] = new long[30];
		int step = (oddThreats ? 2 : 1);
		String name = (oddThreats ? "Odd" : "");
		System.out
				.print("protected int find" + name + "Threat(int player) {\n");
		System.out
				.print("\tlong temp = (player == PLAYER1 ? fieldP1 : fieldP2);\n");
		for (l = 0; l < 7; l++) {
			System.out.print("\tswitch(colHeight[" + l + "])\n\t{\n");
			for (m = 0; m < 6; m++) {
				System.out.print("\t\tcase " + m + ":\n");
				for (k = 0; k < 30; k++)
					array[k] = -1L;
				int r = 0;
				for (k = 0; k < fourRows.length; k++)
					for (i = 0; i < 7; i++)
						for (j = 0; j < 6; j += step)
							if ((fieldMask[i][j] & fourRows[k]) != 0L) {
								long threeRow = fourRows[k]
										& (~fieldMask[i][j]);
								ReturnValue ret = new ReturnValue();

								if (!otherThreat(threeRow, ret)) {
									ret.col1 = i;
									ret.row1 = j;
								}
								if ((fieldMask[l][m] & threeRow) != 0L) {
									long twoRow = threeRow & (~fieldMask[l][m]);
									boolean leave = false;
									if (ret.row1 > 0 && ret.row2 > 0
											&& ret.col1 >= 0 && ret.col2 >= 0) {
										int u;
										for (u = 0; u < 30; u++)
											if (array[u] == twoRow)
												leave = true;
										if (!leave) {
											array[r++] = twoRow;
											String value = toHex(twoRow);
											System.out
													.print("\t\t\tif((temp & 0x"
															+ value
															+ "L) == 0x"
															+ value
															+ "L && (colHeight["
															+ ret.col1
															+ "]<"
															+ ret.row1
															+ " || colHeight["
															+ ret.col2
															+ "]<"
															+ ret.row2
															+ ")) return "
															+ l
															+ ";\n");
										}
									} else if (ret.row1 > 0 && ret.col1 >= 0) {
										int u;
										for (u = 0; u < 30; u++)
											if (array[u] == twoRow)
												leave = true;
										if (!leave) {
											array[r++] = twoRow;
											String value = toHex(twoRow);
											System.out
													.print("\t\t\tif((temp & 0x"
															+ value
															+ "L) == 0x"
															+ value
															+ "L && colHeight["
															+ ret.col1
															+ "]<"
															+ ret.row1
															+ ") return "
															+ l
															+ ";\n");
										}
									} else if (ret.row2 > 0 && ret.col2 >= 0) {
										int u;
										for (u = 0; u < 30; u++)
											if (array[u] == twoRow)
												leave = true;
										if (!leave) {
											array[r++] = twoRow;
											String value = toHex(twoRow);
											System.out
													.print("\t\t\tif((temp & 0x"
															+ value
															+ "L) == 0x"
															+ value
															+ "L && colHeight["
															+ ret.col2
															+ "]<"
															+ ret.row2
															+ ") return "
															+ l
															+ ";\n");
										}
									}
								}

							}
				System.out.print("\t\t\tbreak;\n");
			}
			System.out.print("\t\tdefault:\n\t\t\tbreak;\n\t}\n");
		}
		System.out.print("\treturn (-1);\n}");
	}

	private boolean otherThreat(long feld, ReturnValue ret) {
		int i, j;
		for (i = 0; i < 7; i++)
			for (j = 0; j < 6; j++) {
				if ((fieldMask[i][j] & feld) != 0L) {
					if (i >= 4 || i == 0) // es kann nicht auf 2 Seiten sein
						return false;

					if ((fieldMask[i + 1][j] & feld) != 0L
							&& (fieldMask[i + 2][j] & feld) != 0L) // Horizontal
					{
						ret.col1 = i - 1;
						ret.col2 = i + 3;
						ret.row1 = j;
						ret.row2 = j;
						return true;
					}

					if (j < 3 && j > 0) // Diagonal hoch
						if ((fieldMask[i + 1][j + 1] & feld) != 0L
								&& (fieldMask[i + 2][j + 2] & feld) != 0L) {
							ret.col1 = i - 1;
							ret.col2 = i + 3;
							ret.row1 = j - 1;
							ret.row2 = j + 3;
							return true;
						}

					if (j > 2 && j < 5) // Diagonal runter
						if ((fieldMask[i + 1][j - 1] & feld) != 0L
								&& (fieldMask[i + 2][j - 2] & feld) != 0L) {
							ret.col1 = i - 1;
							ret.col2 = i + 3;
							ret.row1 = j + 1;
							ret.row2 = j - 3;
							return true;
						}
					return false;
				}
			}
		return false;
	}

	String toHex(long wert) {
		String str = new String();
		int x = 15;
		long move, temp, nibble;
		boolean anyway = false;
		for (int i = 15; i >= 0; i--) {
			move = ((long) x) << (i * 4L);
			temp = wert & move;
			nibble = temp >> (i * 4L);
			if (nibble > 0 || anyway) {
				anyway = true;
				if (nibble < 10)
					str += "" + (char) (nibble + '0') + ""; // 0..9
				else
					str += "" + (char) (nibble + 55) + ""; // a..f
			}
		}
		return str;
	}
}

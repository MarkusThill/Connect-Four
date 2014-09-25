package miscellaneous;

import java.util.Arrays;
import java.util.HashSet;
import c4.ConnectFour;

/**
 * * Always do 3 runs for counting the positions. First, count all positions
 * with a yellow stone in the left-bottom-corner, then with a red stone, and
 * last run when left-bottom-corner is empty. This is done, because the Hashset
 * can be reset after each run, and doesn't need that much memory.
 * 
 * @author Markus Thill
 * 
 */
public class CountPositionsC4 extends ConnectFour {
	private int maxDepth = 6;
	private boolean countAll = false;
	private long count = 0;
	HashSet<LongArr> hs = new HashSet<LongArr>(16777216);

	private static final long MASK = 0x20000000000L;
	int run = 0;

	private void tree(int depth, int player) {
		if (depth == maxDepth || countAll) {

			boolean putElement = false;
			switch (run) {
			case 0: // yellow stone in bottom-left corner
				putElement = (fieldP1 & MASK) == MASK;
				break;
			case 1: // red stone in bottom-left corner
				putElement = (fieldP2 & MASK) == MASK;
				break;
			case 2: // no stone in bottom-left corner
				putElement = ((fieldP1 | fieldP2) & MASK) == 0x0L;
				break;

			}

			if (putElement) {
				LongArr key = new LongArr(fieldP1, fieldP2);
				if (hs.add(key)) {
					count++;
					if ((count & 0xFFFFL) == 0xFFFFL) {
						System.out.println("Count until now: " + count);
						System.gc();
					}
				}
			}
			if (depth == maxDepth)
				return;
		}

		int moves[] = generateMoves(player, true);
		for (int i = 0; moves[i] != -1; i++) {
			if (canWin(moves[i]))
				return;
			putPiece(player, moves[i]);
			tree(depth + 1, player == PLAYER1 ? PLAYER2 : PLAYER1);
			removePiece(player, moves[i]);
		}
	}

	/**
	 * @param toPly
	 *            number of plys, for which number positions are calculated
	 * @param countAll
	 *            count all positions, not only leaf-nodes
	 */
	public void countPositions(int toPly, boolean countAll) {
		resetBoard();
		hs.clear();
		maxDepth = toPly;
		count = 0;
		this.countAll = countAll;

		for (run = 0; run < 3; run++) {
			resetBoard();
			hs.clear();
			maxDepth = toPly;
			tree(0, PLAYER1);
		}

		if (!countAll)
			System.out.println("Number of different positions for exactly "
					+ maxDepth + " stones: " + count + "");

		else
			System.out.println("Number of different positions with 0 to "
					+ maxDepth + " stones: " + count + "");
	}

	/**
	 * @param plyRange
	 *            number of plys (range), for which number positions are
	 *            calculated
	 * @param countAll
	 *            count all positions, not only leaf-nodes
	 */
	public void countPositions(int[] plyRange, boolean countAll) {
		for (int i = plyRange[0]; i <= plyRange[1]; i++)
			countPositions(i, countAll);
	}

	private class LongArr {
		private long arr[];

		LongArr(long val1, long val2) {
			arr = new long[] { val1, val2 };
		}

		public int hashCode() {
			return Arrays.hashCode(arr);
		}

		public boolean equals(Object obj) {
			LongArr o = (LongArr) obj;
			return Arrays.equals(arr, o.arr);
		}
	}

	public static void main(String[] args) {
		CountPositionsC4 cp = new CountPositionsC4();
		cp.countPositions(0, false);
		// cp.countPositions(new int[]{0,9},false);
	}
}

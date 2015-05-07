package nTupleTD;

import java.io.Serializable;
import java.util.Arrays;
import java.util.TreeMap;

import adaptableLearningRates.LearningRates.LRCommon;
import miscellaneous.Tools;

//TODO: Datentyp float für LUT verringert Größe um die Hälfte

/**
 *         Implementation of an single N-Tuple. Every single n-Tuple gets an own
 *         look-up-table (lut)
 * 
 * @author Markus Thill
 */
public class NTupleC4 extends WeightSubSet implements Serializable {

	private static final long serialVersionUID = -6790253713208561102L;

	// Number of rows and columns of the C4-Board
	private static final int ROWCOUNT = 6;
	private static final int COLCOUNT = 7;

	// Bitboard-representation of the bottom-row of the c4-board (needed if
	// empty fields are distinguished)
	protected static final long BOTTOMROW = 0x20820820820L;

	// Sampling points of this n-Tuple. Every sampling point is a long-variable
	// with exact one set bit. For the board-representation of C4 check the
	// class ConnectFour. Using n-Tuples in this format allows fast operations
	// on bit-Boards in methods such as getIndex().
	long nTuple[];

	// Sampling Points from 0-41 (1-dim.);
	private int nTuple2Dim[][];

	// Masks, to check whether a sampling point is reachable or not
	private long reachable[];
	// Sampling-Points in the bottom Row are always re
	private long bottomReachable[];

	NTupleC4(Object array, TDParams tdPar, LRCommon lrCommon) {
		super(lrCommon);
		this.tdPar = tdPar;
		Class<? extends Object> arrayClass = array.getClass();
		if (arrayClass == Integer[].class) {
			setNTuple((Integer[]) array);
		} else if (arrayClass == Integer[][].class) {
			setNTuple((Integer[][]) array);
		} else if (arrayClass == Long[].class) {
			setNTuple((Long[]) array);
		} else if (arrayClass == Long.class) {
			setNTuple((Long) ((Long) array));
		} else {
			throw new UnsupportedOperationException(
					"N-Tuple-Array is from the wrong type. Use: Integer[], Integer[][], Long[], Long.");
		}
	}

	protected void createLUTandLR() {
		lut = new float[(int) Math.pow(tdPar.posVals, nTuple.length)];
		if (tdPar.randInitWeights)
			initWeights(true);
		lr = createLearningRates(lut.length);
	}

	// For 2-dim. Array use this indexing:
	// 0,5 1,5 2,5 3,5 4,5 5,5 6,5
	// 0,4 1,4 2,4 3,4 4,4 5,4 6,4
	// 0,3 1,3 2,3 3,3 4,3 5,3 6,3
	// 0,2 1,2 2,2 3,2 4,2 5,2 6,2
	// 0,1 1,1 2,1 3,1 4,1 5,1 6,1
	// 0,0 1,0 2,0 3,0 4,0 5,0 6,0
	/**
	 * @param nTuple
	 *            sampling points (2-dim.)
	 * @param randInit
	 *            true, if all weights shall be initialized randomly
	 */
	private void setNTuple(Integer[][] nTuple) {
		int length = nTuple.length;
		Integer[] nTuple1dim = new Integer[length];

		// Convert in 1-dim. Array
		for (int i = 0; i < length; i++)
			nTuple1dim[i] = nTuple[i][0] * ROWCOUNT + nTuple[i][1];

		setNTuple(nTuple1dim);
	}

	// Use this Board representation:
	// 5 11 17 23 29 35 41
	// 4 10 16 22 28 34 40
	// 3 9 15 21 27 33 39
	// 2 8 14 20 26 32 38
	// 1 7 13 19 25 31 37
	// 0 6 12 18 24 30 36
	/**
	 * @param nTuple
	 *            sampling points (1-dim.)
	 * @param randInit
	 *            true, if all weights shall be initialized randomly
	 */
	private void setNTuple(Integer[] nTuple) {
		int length = nTuple.length;
		Long[] nTupleMasks = new Long[length];

		// Convert 1-dim Array in a array of long-masks
		for (int i = 0; i < length; i++) {
			long mask = 1L << (ROWCOUNT * COLCOUNT - 1 - nTuple[i]);
			nTupleMasks[i] = mask;
		}
		setNTuple(nTupleMasks);
	}

	// All Sample-Points in one variable
	// Use this BitBoard-Representation:
	// 36 30 24 18 12 06 00
	// 37 31 25 19 13 07 01
	// 38 32 26 20 14 08 02
	// 39 33 27 21 15 09 03
	// 40 34 28 22 16 10 04
	// 41 35 29 23 17 11 05
	/**
	 * @param nTuple
	 *            sampling points (all bits set in one long)
	 * @param randInit
	 *            true, if all weights shall be initialized randomly
	 */
	private void setNTuple(Long nTuple) {
		// First: count set Bits
		long tmp = nTuple;
		int length = 0;
		while (tmp != 0) {
			length++;
			tmp = tmp & (tmp - 1L);
		}

		Long[] nTupleMasks = new Long[length];
		long newNTuple;

		// Now convert to 1-dim. array of masks
		for (int i = 0; i < length; i++) {
			newNTuple = nTuple & (nTuple - 1L);
			nTupleMasks[i] = nTuple ^ newNTuple;
			nTuple = newNTuple;
		}
		setNTuple(nTupleMasks);
	}

	// Parameter is a List of masks with one set Bit
	// Use this BitBoard-Representation:
	// 36 30 24 18 12 06 00
	// 37 31 25 19 13 07 01
	// 38 32 26 20 14 08 02
	// 39 33 27 21 15 09 03
	// 40 34 28 22 16 10 04
	// 41 35 29 23 17 11 05
	/**
	 * This method is called by all other setNTuple-methods!!!
	 * 
	 * @param nTuple
	 *            sampling points (bit-masks)
	 * @param randInit
	 *            true, if all weights shall be initialized randomly
	 */
	/**
	 * @param nTuple
	 * @param randInit
	 */
	private void setNTuple(Long[] nTuple) {
		this.nTuple = Tools.toPrimitiveLong(nTuple);
		this.nTuple2Dim = Tools.toPrimitiveInt(getNTuple2dim());
		createReachablePoints();
		createLUTandLR();
		if (useElig)
			eligTraces = new TreeMap<Integer, EligibilityTrace>();
	}

	/**
	 * Reachable-Points are one below the actual sampling points. These points
	 * are needed if 4 possible values per field are used.
	 */
	public void createReachablePoints() {
		reachable = new long[nTuple.length];
		bottomReachable = new long[nTuple.length];

		for (int i = 0; i < reachable.length; i++) {
			// Reachable Point can't be below the bottom-row
			if ((nTuple[i] & BOTTOMROW) != 0L)
				bottomReachable[i] = nTuple[i];
			else
				reachable[i] = (nTuple[i] << 1);
		}
	}

	/**
	 * @return the number of non-zero weights in LUT
	 */
	// /WK/
	public long countNonZeroLUTWeights() {
		long count = 0;
		for (int i = 0; i < lut.length; i++)
			if (lut[i] != 0)
				count++;
		return count;
	}

	/**
	 * @return the number of realizable states (states of the n-tuple which can
	 *         happen in Connect-4). For example (0,1,1,2) from bottom to top is
	 *         a non-realizable state for a column, because the lowest cell can
	 *         not be empty, if higher ones are filled. (1,1,0) is a realizable
	 *         state.
	 * 
	 * @author Wolfgang Konen /WK/
	 */
	public long countRealizableStates() {
		int c, i, r, x, y;
		Integer[][] twoDim = getNTuple2dim();
		// twoDim[i][0]: the col of the ith n-tuple position
		// twoDim[i][1]: the row of the ith n-tuple position
		//
		// For 2-dim. Array use this indexing:
		// 0,5 1,5 2,5 3,5 4,5 5,5 6,5
		// 0,4 1,4 2,4 3,4 4,4 5,4 6,4
		// 0,3 1,3 2,3 3,3 4,3 5,3 6,3
		// 0,2 1,2 2,2 3,2 4,2 5,2 6,2
		// 0,1 1,1 2,1 3,1 4,1 5,1 6,1
		// 0,0 1,0 2,0 3,0 4,0 5,0 6,0
		long count = 1;
		int f = (getPosVals() == 4 ? 1 : 0);
		for (c = 0; c < COLCOUNT; c++) {
			int[] colPlaces = new int[ROWCOUNT];
			for (i = 0; i < getLength(); i++)
				if (twoDim[i][0] == c)
					colPlaces[twoDim[i][1]] = 1;
			// colPlaces[r] is 1, if row r of current column belongs to n-tuple,
			// else 0
			// ... well, this recursion is a bit complicated to explain, but it
			// works

			int cCount = 1;
			for (x = 0, r = ROWCOUNT; r > 0; r--) {
				y = colPlaces[r - 1];
				cCount = (1 + f * (1 - y)) * x + (x + 1) * cCount;
				x = y; // for next pass through r-loop
			}
			// cCount: number of realizable states in column c
			cCount = x + (x + 1) * cCount; 
			// count: number of realizable states for this n-tuple
			count *= cCount; 
		}
		return count;
	}

	/**
	 * Get the LUT-Index for one board. If possible, use the other provided
	 * method, to get a better performance (if a good performance is needed)!
	 * 
	 * @param board
	 *            0-> Empty <br>
	 *            1 -> Yellow (X) <br>
	 *            2 -> Red (O) <br>
	 *            3 -> Empty but directly reachable <br>
	 * @return LUT-Index
	 */
	public int getFeature(int[][] board) {
		int posVals = tdPar.posVals;
		int pow = 1;
		int index = 0;
		for (int i = 0; i < nTuple.length; i++) {
			index += pow * board[nTuple2Dim[i][0]][nTuple2Dim[i][1]];
			pow *= posVals;
		}
		return index;
	}

	public int getFeature(int[][] board, boolean mirror) {
		int pow = 1;
		int index = 0;
		int posVals = tdPar.posVals;

		if (!mirror) {
			for (int i = 0; i < nTuple.length; i++) {
				index += pow * board[nTuple2Dim[i][0]][nTuple2Dim[i][1]];
				pow *= posVals;
			}
		} else {
			for (int i = 0; i < nTuple.length; i++) {
				int x = (COLCOUNT - 1) - nTuple2Dim[i][0];
				int y = nTuple2Dim[i][1];
				index += pow * board[x][y];
				pow *= posVals;
			}
		}
		return index;
	}

	/**
	 * Use the Connect-Four-Class to convert other Board-Representations to this
	 * // BitBoard-Representation
	 * 
	 * @param fieldP1
	 *            Bit-Board-representation of player ones field
	 * @param fieldP2
	 *            Bit-Board-representation of player twos field
	 * @return index in the lookup-table (LUT)
	 */
	public int getFeature(long fieldP1, long fieldP2) {
		int pow = 1;
		int index = 0;
		long f1f2;
		int posVals = tdPar.posVals;
		for (int i = 0; i < nTuple.length; i++) {
			if ((fieldP1 & nTuple[i]) != 0L)
				index += pow; // pow * 1 -> for player 1
			else if ((fieldP2 & nTuple[i]) != 0L)
				index += (pow << 1); // pow * 2 -> for player 2
			else if (posVals == 4) {
				// pow * 3 -> for empty but reachable field
				f1f2 = fieldP1 | fieldP2;
				if (((f1f2 ^ BOTTOMROW) & bottomReachable[i]) != 0L)
					index += (pow * 3); // (pow << 1) + pow
				else if ((f1f2 & reachable[i]) != 0L)
					index += (pow * 3); // (pow << 1) + pow
			}
			// index += 0 -> for emtpy field
			pow *= posVals;
		}
		return index;
	}

	/**
	 * Get the game board corresponding to this LUT index for this NTuple.
	 * 
	 * @param index
	 *            the index into LUT
	 * @return the corresponding game board (vector of length 9, carrying -1
	 *         ("O"), 0 (empty) or +1 ("X")). As a specialty of this function,
	 *         each board cell which is NOT a NTuple position, gets a "-1"
	 */
	public int[][] getBoard(int index) {
		int board[][] = new int[COLCOUNT][ROWCOUNT];
		int i, j;
		int posVals = tdPar.posVals;
		for (i = 0; i < COLCOUNT; i++)
			for (j = 0; j < ROWCOUNT; j++)
				board[i][j] = -1; // Not used

		Integer[][] nTuple = getNTuple2dim();
		int x, y;
		for (i = 0; i < nTuple.length; i++) {
			x = nTuple[i][0];
			y = nTuple[i][1];
			board[x][y] = index % posVals;
			index = (index - (int) board[x][y]) / posVals;
		}
		return board;
	}

	public boolean isPartOfNTuple(int x, int y) {
		return getPowSamplingPoint(x, y) != -1;
	}

	public int getPowSamplingPoint(int x, int y) {
		Integer nTuple[][] = getNTuple2dim();
		int pow = 1;
		int posVals = tdPar.posVals;
		for (int i = 0; i < nTuple.length; i++) {
			if (nTuple[i][0] == x && nTuple[i][1] == y)
				return pow;
			pow *= posVals;
		}
		return -1; // Point is not part of the N-Tuple
	}

	/**
	 * @return Possible Values for one field of the board
	 */
	public int getPosVals() {
		return tdPar.posVals;
	}

	/**
	 * @return Tuple-Length
	 */
	public int getLength() {
		return nTuple.length;
	}

	/**
	 * @return Sampling points as masks
	 */
	public long[] getNTupleMasks() {
		return nTuple;
	}

	/**
	 * @return Sampling points (all bits set in one long)
	 */
	public long getNTuple() {
		long masks = 0L;
		for (int i = 0; i < nTuple.length; i++)
			masks |= nTuple[i];
		return masks;
	}

	/**
	 * @return Sampling points (1-dim. Array)
	 */
	public Integer[] getNTuple1dim() {
		int length = nTuple.length;
		Integer[] nt = new Integer[length];
		for (int i = 0; i < length; i++)
			nt[i] = COLCOUNT * ROWCOUNT - 1 - Tools.ld(nTuple[i]);
		return nt;
	}

	/**
	 * @return Sampling points (2-dim. Array)
	 */
	public Integer[][] getNTuple2dim() {
		Integer oneDim[] = getNTuple1dim();
		return getNTuple2dim(oneDim);
	}

	/**
	 * @return Sampling points (2-dim. Array)
	 */
	public static Integer[][] getNTuple2dim(Integer oneDim[]) {
		int length = oneDim.length;
		Integer twoDim[][] = new Integer[length][2];
		for (int i = 0; i < length; i++) {
			twoDim[i][0] = oneDim[i] / ROWCOUNT;
			twoDim[i][1] = oneDim[i] % ROWCOUNT;
		}
		return twoDim;
	}

	/**
	 * @param dim
	 *            Dimension (1,2) for output
	 * @return Sampling points as String
	 */
	public String toString(int dim) {
		if (dim == 1) {
			return Arrays.toString(getNTuple1dim());

		} else {
			Integer[][] twoDim = getNTuple2dim();
			String str = new String();
			for (int i = 0; i < twoDim.length; i++)
				str += Arrays.toString(twoDim[i]);
			return str;
		}
	}
}

package nTupleTD;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

public class NTupleFactory {
	
	// Number of columns in a c4-board
	private static final int NUMCOLS = 7;

	// Number of Rows in a c4-board
	private static final int NUMROWS = 6;
	
	/**
	 * Generate all n-Tuples with their sampling-points. Random Points on the
	 * board.
	 * 
	 * @param tupleLen
	 *            max. tuple-Length
	 * @param tupleNum
	 *            Number of n-Tuples to be generated
	 * @param randLength
	 *            true, if tuple-lengths shall be in chosen randomly in range 2
	 *            .. tupleLen
	 * @return List of n-Tuples with random sampling-points
	 */
	public static Integer[][] generateRandomTuples(int tupleLen, int tupleNum,
			boolean randLength) {
		Integer[][] nTuples = new Integer[tupleNum][];
		for (int i = 0; i < tupleNum; i++) {
			boolean isUnique;
			Integer[] tuple;
			do {
				isUnique = true;
				tuple = generateRandomPoints(tupleLen, randLength);

				// Not likely, but check if this n-Tupel already exists
				for (int j = 0; j < i; j++)
					if (Arrays.equals(tuple, nTuples[j]))
						isUnique = false;
			} while (!isUnique);
			nTuples[i] = tuple;
		}
		return nTuples;
	}

	/**
	 * Generate a single n-Tuple. Choose random Points on the board.
	 * 
	 * @param tupleLen
	 *            max. tuple-Length
	 * @param randLength
	 *            true, if tuple-lengths shall be in chosen randomly in range 2
	 *            .. tupleLen
	 * @return single n-Tuple with its sampling-points
	 */
	public static Integer[] generateRandomPoints(int tupleLen, boolean randLength) {
		Random rand = new Random();
		if (randLength) // Length in range 2..tupleLen
			tupleLen = rand.nextInt(tupleLen - 1) + 2;

		Integer[] tuple = new Integer[tupleLen];
		for (int i = 0; i < tupleLen; i++) {
			int sp;
			boolean isUnique;
			do {
				isUnique = true;
				sp = rand.nextInt(NUMROWS * NUMCOLS);
				for (int k = 0; k < i; k++)
					if (tuple[k] == sp) {
						isUnique = false;
						break;
					}
			} while (!isUnique);
			tuple[i] = sp;
		}
		Arrays.sort(tuple);
		return tuple;
	}

	/**
	 * Generate all n-Tuples with their sampling-points. Random-Walk on the
	 * board.
	 * 
	 * @param tupleLen
	 *            max. tuple-Length
	 * @param tupleNum
	 *            Number of n-Tuples to be generated
	 * @param randLength
	 *            true, if tuple-lengths shall be in chosen randomly in range 2
	 *            .. tupleLen
	 * @return List of n-Tuples consisting of random walks
	 */
	public static  Integer[][] generateRandomWalks(int tupleLen, int tupleNum,
			boolean randLength) {
		Integer[][] nTuples = new Integer[tupleNum][];
		for (int i = 0; i < tupleNum; i++) {
			boolean isUnique;
			Integer[] tuple;
			do {
				isUnique = true;
				tuple = generateSingleRandomWalk(tupleLen, randLength);

				// Not likely, but check if this n-Tupel already exists
				if (tuple != null)
					for (int j = 0; j < i; j++)
						if (Arrays.equals(tuple, nTuples[j]))
							isUnique = false;
			} while (!isUnique || tuple == null);
			nTuples[i] = tuple;
		}

		boolean DEBG = false;
		if (DEBG) {
			countSamplingPoints(nTuples);
			// TDParams f = new TDParams();
			// countSamplingPoints(f.nTuples);
			// debg();
		}

		return nTuples;
	}

	/**
	 * Generate a single n-Tuple. Make a random-walk on the board.
	 * 
	 * @param tupleLen
	 *            max. tuple-Length
	 * @param randLength
	 *            true, if tuple-lengths shall be in chosen randomly in range 2
	 *            .. tupleLen
	 * @return single n-Tuple with its random-walk
	 */
	public static Integer[] generateSingleRandomWalk(int tupleLen,
			boolean randLength) {
		Random rand = new Random();

		int tmpTuple[][] = new int[tupleLen][2/* x,y */];
		int realTupleLen = 0;

		// Generate starting-point
		int x, y;
		x = rand.nextInt(NUMCOLS);
		y = rand.nextInt(NUMROWS);

		tmpTuple[realTupleLen][0] = x; // X
		tmpTuple[realTupleLen][1] = y; // Y
		realTupleLen++;

		// generate remaining points
		for (int i = 1; i < tupleLen; i++) {
			boolean isValid, skip;
			int dx, dy;
			int trys = 0;
			do {
				isValid = true;
				skip = false;
				dx = rand.nextInt(3) - 1;
				dy = rand.nextInt(3) - 1;
				if (dx == 0 && dy == 0)
					isValid = false;
				if (x + dx < 0 || x + dx >= NUMCOLS)
					isValid = false;
				if (y + dy < 0 || y + dy >= NUMROWS)
					isValid = false;
				if (!randLength && isValid) {
					if (contains(tmpTuple, realTupleLen, x + dx, y + dy))
						isValid = false;
				} else if (randLength && isValid) {
					if (contains(tmpTuple, realTupleLen, x + dx, y + dy))
						skip = true;
				}
				trys++;
			} while (!isValid && trys < 20);

			if (!skip && isValid) {
				x += dx;
				y += dy;
				tmpTuple[realTupleLen][0] = x; // X
				tmpTuple[realTupleLen][1] = y; // Y
				realTupleLen++;
			} else if (!randLength)
				return null;
		}

		// Copy to Array with the real Length
		// and convert to one-dim. Array
		Integer[] tuple = new Integer[realTupleLen];
		for (int i = 0; i < tuple.length; i++)
			tuple[i] = tmpTuple[i][0] * NUMROWS + tmpTuple[i][1];

		Arrays.sort(tuple);
		return tuple;
	}

	/**
	 * Check, how often each of the 42 possible Sampling-points is used in a
	 * list of n-Tuples. For DEBUG-purposes.
	 * 
	 * @param tupleList
	 */
	private static void countSamplingPoints(Integer[][] tupleList) {
		float[] samplePoint = new float[NUMCOLS * NUMROWS];
		int numSamplePoints = 0;
		for (int i = 0; i < tupleList.length; i++)
			for (int j = 0; j < tupleList[i].length; j++) {
				numSamplePoints++;
				samplePoint[tupleList[i][j]]++;
			}

		for (int i = 0; i < samplePoint.length; i++) { // in percent
			samplePoint[i] = samplePoint[i] * 100 / numSamplePoints;
		}

		// Board representation:
		// 5 11 17 23 29 35 41
		// 4 10 16 22 28 34 40
		// 3 9 15 21 27 33 39
		// 2 8 14 20 26 32 38
		// 1 7 13 19 25 31 37
		// 0 6 12 18 24 30 36
		System.out.println("Usage of every Sampling Point in Percent...");
		DecimalFormat df = new DecimalFormat("00.00");
		for (int j = 5; j >= 0; j--) {
			for (int i = 0; i < 7; i++) {
				System.out.print(df.format(samplePoint[i * 6 + j]) + "\t");
			}
			System.out.println();
		}
	}
	
	// only for arrays of the type field[][2]
	/**
	 * Check if a Array already contains a x,y-combination
	 * 
	 * @param field
	 *            2-dim array (format: field[][2])
	 * @param length
	 *            Size of the field
	 * @param x
	 *            first param
	 * @param y
	 *            second param
	 * @return true, if array field contains the x,y-combination
	 */
	private static boolean contains(int field[][], int length, int x, int y) {
		for (int i = 0; i < length; i++)
			if (field[i][0] == x && field[i][1] == y)
				return true;
		return false;
	}
}

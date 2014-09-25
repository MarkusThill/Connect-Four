package miscellaneous;

import java.util.Arrays;

import nTupleTD.NTupleC4;
import nTupleTD.NTupleFactory;

/**
 * @author central method: Wolfgang Konen /WK/
 * 
 */
public class CountRealizableStates {

	// Number of rows of the C4-Board
	private static final int ROWCOUNT = 6;

	// Number of columns of the C4-Board
	private static final int COLCOUNT = 7;

	/**
	 * @return the number of realizable states (states of the n-tuple which can
	 *         happen in Connect-4). For example (0,1,1,2) from bottom to top is
	 *         a non-realizable state for a column, because the lowest cell can
	 *         not be empty, if higher ones are filled. (1,1,0) is a realizable
	 *         state.
	 * 
	 * @author Wolfgang Konen /WK/
	 */
	public static long countRealizableStates(Integer[][] twoDim, int posVals) {
		int c, i, r, x, y;

		// For 2-dim. Array use this indexing:
		// 0,5 1,5 2,5 3,5 4,5 5,5 6,5
		// 0,4 1,4 2,4 3,4 4,4 5,4 6,4
		// 0,3 1,3 2,3 3,3 4,3 5,3 6,3
		// 0,2 1,2 2,2 3,2 4,2 5,2 6,2
		// 0,1 1,1 2,1 3,1 4,1 5,1 6,1
		// 0,0 1,0 2,0 3,0 4,0 5,0 6,0
		long count = 1;

		int f = (posVals == 4 ? 1 : 0);
		for (c = 0; c < COLCOUNT; c++) {
			int[] colPlaces = new int[ROWCOUNT];
			for (i = 0; i < twoDim.length; i++)
				if (twoDim[i][0] == c)
					colPlaces[twoDim[i][1]] = 1;
			// colPlaces[r] is 1, if row r of current column belongs to n-tuple,
			// else 0

			//
			// ... well, this recursion is a bit complicated to explain, but it
			// works
			//
			int cCount = 1;
			for (x = 0, r = ROWCOUNT; r > 0; r--) {
				y = colPlaces[r - 1];
				cCount = (1 + f * (1 - y)) * x + (x + 1) * cCount;
				x = y; // for next pass through r-loop
			}
			cCount = x + (x + 1) * cCount; // cCount: number of realizable
											// states in column c

			count *= cCount; // count: number of realizable states for this
								// n-tuple
		}
		// }
		// if (getPosVals()==3) assert(count2==count);

		return count;
	}

	public static double realizableStatesRatio(int posVals, int genMode,
			int tupleLen, int numRuns, boolean DEBG) {
		double realizableRatio = 0.0;
		for (int i = 0; i < numRuns; i++) {
			Integer[] tuple;
			do {
				if (genMode == 0)
					tuple = NTupleFactory.generateSingleRandomWalk(tupleLen,
							false);
				else
					tuple = NTupleFactory.generateRandomPoints(tupleLen, false);
			} while (tuple == null);
			Integer[][] tuple2Dim = NTupleC4.getNTuple2dim(tuple);
			long count = CountRealizableStates.countRealizableStates(tuple2Dim,
					posVals);
			double lastRatio = count / Math.pow(posVals, tupleLen);
			realizableRatio += lastRatio;

			if (DEBG) {
				System.out.println("===========================");
				System.out.println("Run: " + i);
				System.out.println(Arrays.toString(tuple));
				System.out.println("Realizable States: " + count);
				System.out.println("Ratio: " + lastRatio);
				System.out.println("===========================\n");
			}
		}
		return realizableRatio / numRuns;
	}

	public static void completeRun() {
		int RUN = 10000000;
		boolean DEBG = false;
		System.out.println("Realizable States in Percent...");
		for (int genMode = 0; genMode <= 1; genMode++) {
			if (genMode == 0)
				System.out.println("Random Walk:\n==========");
			else
				System.out.println("Random Points:\n==========");
			for (int posVals = 3; posVals <= 4; posVals++) {
				if (posVals == 3)
					System.out.println("m=3:\n--------");
				else
					System.out.println("m=4:\n--------");
				for (int tupleLen = 1; tupleLen <= 15; tupleLen++) {
					double result = realizableStatesRatio(posVals, genMode,
							tupleLen, RUN, DEBG);
					System.out.println("tupleLen: " + tupleLen + ", Result: \t"
							+ result);
				}
				System.out.println();
			}
		}
	}

	public static double meanTupleLength(int maxTupleLen, int numRuns,
			boolean DEBG) {
		double meanTupleLen = 0.0;
		//int genMode = 0; // Always Random-Walk
		for (int i = 0; i < numRuns; i++) {
			Integer[] tuple;
			do {
				//if (genMode == 0)
					tuple = NTupleFactory
							.generateSingleRandomWalk(maxTupleLen, true);
				//else
				//	tuple = ValueFuncC4.generateRandomPoints(maxTupleLen, true);
			} while (tuple == null);
			meanTupleLen += tuple.length;
		}
		return meanTupleLen / numRuns;
	}
	
	

	public static void main(String[] args) {
		// int[] tuple = ValueFuncC4.generateSingleRandomWalk(8,false);
		// System.out.println(Arrays.toString(tuple));
		// CountRealizableStates.countRealizableStates(NTupleC4.getNTuple2dim(tuple));

		Integer[] tuple = new Integer[] { 0, 6, 7, 12, 13, 14, 19, 21 };
		// int[] tuple = new int[]{6, 7, 13, 14, 19, 24, 25, 30};
		// int[] tuple = new int[]{18, 19, 24, 26, 30, 31, 36, 37};
		Integer[][] dim2 = NTupleC4.getNTuple2dim(tuple);
		System.out
				.println(CountRealizableStates.countRealizableStates(dim2, 4));

		// double ratio = realizableStatesRatio(4, 0, 5, 100000, false);
		// System.out.println("Percent of Realizable States: " + ratio);

		//completeRun();
		
		System.out.println(CountRealizableStates.meanTupleLength(3, 100000, false));
	}
}

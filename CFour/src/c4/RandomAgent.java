package c4;

import java.util.Random;

/**
 * Implementation of an random-Agent. All returned moves are legal, but
 * completely random. The values generated for the boards lie in the range -1 ..
 * +1
 * 
 * @author Markus Thill
 */
public class RandomAgent extends ConnectFour implements Agent {

	Random rnd = new Random();

	// Binary Semaphore, to prevent multiple access (e.g. by parallel threads)
	// private Semaphore mutex = new Semaphore(1);

	/*
	 * (non-Javadoc)
	 * 
	 * @see c4.Agent#getBestMove(int[][])
	 */
	@Override
	public int getBestMove(int[][] table) {
		// TODO: semOpDown();
		double[] values = getNextVTable(table, false);
		int bestMove = 0;
		double bestVal = -1000.0;
		for (int i = 0; i < COLCOUNT; i++)
			if (colHeight[i] < ROWCOUNT && values[i] > bestVal) {
				bestVal = values[i];
				bestMove = i;
			}
		// TODO:semOpUp();
		return bestMove;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see c4.Agent#getScore(int[][], boolean)
	 */
	@Override
	public double getScore(int[][] table, boolean useSigmoid) {
		// TODO:semOpDown();
		// Always in Range -1 .. +1
		double val = rnd.nextDouble() * 2 - 1;
		// TODO:semOpUp();
		return val;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see c4.Agent#getNextVTable(int[][], boolean)
	 */
	@Override
	public double[] getNextVTable(int[][] table, boolean useSigmoid) {
		// TODO:semOpDown();
		double[] values = new double[COLCOUNT];

		setBoard(table);

		for (int i = 0; i < values.length; i++)
			values[i] = (colHeight[i] < ROWCOUNT ? rnd.nextDouble() * 2 - 1
					: Double.NaN);
		// TODO:semOpUp();
		return values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see c4.Agent#getName()
	 */
	@Override
	public String getName() {
		return new String("Random-Agent");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see c4.Agent#semOpDown()
	 */
	@Override
	public void semOpDown() {
		// try {
		// mutex.acquire();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see c4.Agent#semOpUp()
	 */
	@Override
	public void semOpUp() {
		// mutex.release();
	}

	@Override
	public AgentState getAgentState() {
		// TODO Auto-generated method stub
		return null;
	}

}

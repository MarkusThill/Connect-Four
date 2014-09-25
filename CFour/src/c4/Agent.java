package c4;

/**
 * An Interface-Implementation for Connect-Four Agents
 * 
 * @author Markus Thill
 * 
 */
public interface Agent {

	public enum AgentState {
		UNINITIALIZED, INITIALIZED, TRAINED
	};

	/**
	 * @return The current state of the Agent.
	 */
	public AgentState getAgentState();

	/**
	 * @param table
	 *            current board
	 * @return Best move for the current Player
	 */
	public int getBestMove(int[][] table);

	/**
	 * Based on an already existing VTable e.g.,
	 * {@link Agent#getNextVTable(int[][], boolean)}, select the best move for
	 * the player to move. In some cases the agent determines the values for all
	 * after-states of a position. If all these values are already available it
	 * is not necessary to call the method {@link Agent#getBestMove(int[][])},
	 * which often would need extra time. Example: If all the values for the
	 * after-states were already calculated by a treesearch-algorithm, then we do
	 * not have to start the search again, just to find the best move, since the
	 * best move is simply determined by the after-state which maxizimizes the
	 * final outcome.
	 * 
	 * @param player
	 *            player, to find best move for
	 * @param colHeight
	 *            an Array containing the heights of all columns
	 * @param vTable
	 *            Values for all columns
	 * @return Best move for a player using the param vTable
	 */
	public int getBestMove(int player, int[] colHeight, double[] vTable);

	/**
	 * For a given position determine the game-theoretic value, thus the outcome
	 * of the game, starting from this position. The implementation should be
	 * able to scale the score into the range -1 (Win Red) and +1 (Win Yellow)
	 * with 0 indicating a draw.
	 * 
	 * @param table
	 *            7x6-board
	 * @param putInRange
	 *            put score in range -1 .. +1
	 * @return Score (Value) for the given board
	 */
	public double getScore(int[][] table, boolean putInRange);

	/**
	 * Determine for a given position the values of all possible actions (after-states). 
	 * @param table
	 *            7x6-board
	 * @param putInRange
	 *            put values in range -1 .. +1
	 * @return table containing the values for each column of the board.
	 */
	public double[] getNextVTable(int[][] table, boolean putInRange);

	/**
	 * @return Name of the Agent
	 */
	public String getName();

	/**
	 * If Threads are used, which multiply access the agents, then ensure that
	 * only one thread has mutual exclusive access. This is the DOWN-Operation on the
	 * semaphore (MUTEX). This method should be used in combination with {@link Agent#semOpUp()}.
	 */
	public void semOpDown();

	/**
	 * If Threads are used, which multiply access the agents, then ensure that
	 * only one thread has mutual exclusive access. This is the UP-Operation on the
	 * semaphore (MUTEX). This method should be used in combination with {@link Agent#semOpDown()}.
	 */
	public void semOpUp();

}

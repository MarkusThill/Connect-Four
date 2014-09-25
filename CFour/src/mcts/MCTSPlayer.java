package mcts;

import c4.Agent;

public class MCTSPlayer implements Agent {

	/**
	 * Root of the tree.
	 */
	public TreeNode rootNod = null;
	private MCTSParams mctsPar = null;

	public MCTSPlayer(MCTSParams par) {
		mctsPar = par;
	}

	public void init(int[][] position, boolean useOldSubtree) {
		GameState s = new GameState(position);

		if (useOldSubtree && rootNod != null) {
			// Try to find the current position in the old tree (only until a
			// certain depth)
			TreeNode nod = rootNod.find(s, 6);
			rootNod = nod;
			if (rootNod != null)
				return;
			// assert rootNod != null :
			// "Normally, the current position should be found in the old tree";
		}
		// Set the game observation to a newly root node.
		rootNod = new TreeNode(null, s);
		System.gc();
	}

	public int run(boolean mostVisitedAction) {
		// TODO: Later adjust to time-limits
		// TODO: Do sth with bestChild
		@SuppressWarnings("unused")
		TreeNode bestChild = rootNod.mctsSearch((int) mctsPar.constraintValue);
		int mostVisited = rootNod.mostVisitedAction();
		int bestAction = rootNod.bestAction();
		
		if (mostVisited != bestAction)
			System.out.println("Most visited action is: " + mostVisited
					+ ", but best action is: " + bestAction + "!");
		
		switch(mctsPar.actionSelection) {
		case BESTVALUE:
			return bestAction;
		case MOSTVISITED:
			return mostVisited;
		default:
			throw new UnsupportedOperationException("Not supported yet");
		
		}
	}

	@Override
	public AgentState getAgentState() {
		return AgentState.INITIALIZED;
	}

	@Override
	public int getBestMove(int[][] table) {
		// Create Game-state from table
		init(table, true);
		return run(true);
	}

	@Override
	public int getBestMove(int player, int[] colHeight, double[] vTable) {
		return Integer.MIN_VALUE;
	}

	@Override
	public double getScore(int[][] table, boolean useSigmoid) {
		// Create Game-state from table
		init(table, true);
		run(true);
		return rootNod.bestValue();
	}

	@Override
	public double[] getNextVTable(int[][] table, boolean useSigmoid) {
		return rootNod.allActionValues();
	}

	@Override
	public String getName() {
		return "MCTS Agent";
	}

	@Override
	public void semOpDown() {
		// TODO Auto-generated method stub
	}

	@Override
	public void semOpUp() {
		// TODO Auto-generated method stub
	}

}

package mcts;

public class MCTSParams {

	// Two Search constraints are possible
	// With a time-constraint the mcts-search aborts after a given max. time,
	// otherwise after a certain amount of iterations
	public static enum ConstraintType {
		TIME, ITERATION
	};

	public ConstraintType constraintType = ConstraintType.ITERATION;
	public double constraintValue = 5e5;

	// How to select the action? Two possibilities: Select the most visted
	// action from the current node, or the action which leads to a node with
	// the best value. Typically, both should lead to the same next node.
	public static enum ActionSelection {
		MOSTVISITED, BESTVALUE
	};

	public ActionSelection actionSelection = ActionSelection.MOSTVISITED;

	// Constant C in the UCB1-formula, which weights the exploration of the
	// mcts-search
	public double ucbConstant = Math.sqrt(2);

	// Use the subtrees of previous moves (if they can be found). If the subtree
	// can be found, the earlier results are basis of the new mcts-search. So
	// the mcts-search does not start with an empty tree.
	public boolean useOldSubtree = true;

}

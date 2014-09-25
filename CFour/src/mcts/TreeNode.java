package mcts;

import java.util.Arrays;
import java.util.Random;

import c4.ConnectFour;

// This class is following the pseudo-code from Cameron Browne: 
// http://ccg.doc.gold.ac.uk/teaching/ludic_computing/ludic16.pdf
// Slide 16

// TODO: 1. How are permutations handled in MCTS? Different mode sequences can lead to the same state. What about transposition tables?
// TODO: 2. Can we take the selected subtree of the MCTS-search of the previous state as basis for the new search?
// TODO: 3. What about he uct-variant Epsilon-Greedy?
// TODO: 4. What about a few smaller trees (instead of one big tree) with a voting technique (similar to RF)?
// TODO: 5. Decaying reward in backpropagation step

public class TreeNode {
	private static final double EPS = 1e-6;
	private static final double C = 2 * Math.sqrt(2);
	private static final Random RAND = new Random(); // Random generator
	private static final boolean DEBUG = true;

	// Game-State of the node
	// TODO: Do we really have to store the whole state-information? Is it not
	// enough just to store the action,
	// since the state is defined by a sequence of actions?
	// Leave it as this at the moment
	GameState state;

	private TreeNode parent; // The parent-node of the tree
	private TreeNode[] children; // An array of children. (Linked) List instead
									// of array?
	private int nVisits; // how often was this node already visited?
	private double reward; // accumulated reward (TODO: float sufficient?)
	private int depth; // In which depth is this node?

	public TreeNode(TreeNode parent, GameState s) {
		this.state = s;
		// Parent node
		this.parent = parent;

		// Create empty children for all possible actions
		// Needs some memory for all references. But leave it like this in the
		// beginning
		children = new TreeNode[s.getNumActions()];

		nVisits = 0;
		reward = 0.0;
		if (parent != null)
			depth = parent.depth + 1;

	}

	public TreeNode mctsSearch(final int iterations) {
		// If Agent can win, then we have a terminal state
		int x = state.findImmediateThreat();
		if (x != -1) {
			for (int i = 0; i < state.getNumActions(); i++) {
				if (state.getAction(i) == x) {
					GameState s = state.getCopy(false);
					s.advance(x);
					children[i] = new TreeNode(this, s);
					children[i].reward = -1;
					children[i].nVisits = 1;
					return children[i];
				}
			}
			throw new UnsupportedOperationException("Error");
		}
		// Create initial root node v_0 for state s_0
		// ....

		// Repeat mcts Search until limit (iterations) is reached
		for (int i = 0; i < iterations; i++) {
			// 1. First apply tree-policy to v_0 (returns new expanded node vl)
			TreeNode nod = treePolicy();

			// 2. Apply default policy (roll-out) to node vl (returns reward)
			double reward = nod.defaultPolicy();

			// 3. Backpropagate reward
			backpropagate(nod, reward);
		}

		// Return best child of v0
		return bestChild();
	}

	TreeNode treePolicy() {
		// select a node based on the tree-policy
		TreeNode nod = this;

		// While state is not a terminal state
		// TODO: Give a max. certain tree-horizon as well later
		while (!nod.state.isTerminal()) {
			// If not fully expanded, then expand now
			if (!nod.isFullyExpanded()) {
				return nod.expand();
			}
			// Otherwise select node based on UCT
			nod = nod.bestChild();
		}
		return nod;
	}

	TreeNode expand() {
		// find an untried action. The index of every child corresponds to one
		// possible action from the current state
		int i = 0;
		while (i < children.length && children[i] != null)
			i++;

		// Get a copy of the current state
		GameState s = state.getCopy(false);

		// select action for the new child
		int action = state.getAction(i);

		// perform action on current state to advance to the next state
		s.advance(action);

		// Create a new node
		TreeNode newNod = new TreeNode(this, s);

		// Add to child-list
		children[i] = newNod;

		return newNod;
	}

	public TreeNode bestChild() {
		// Select most urgent node with UCB
		TreeNode bestChild = null;
		double bestUCB = Double.NEGATIVE_INFINITY;

		for (TreeNode child : this.children) {
			// The values of the child are seen from the view of the opponent
			// (always from the player to move). Therefore, his reward is my
			// loss and vice versa. We have to invert his reward.
			double childValue = -child.reward / child.nVisits;

			// explorative term: add 1 to this.nVisits, since the value is
			// incremented later. The factor 2 in the sqrt seen in a few
			// references can also be
			// seen as a part of c
			double N_v = this.nVisits + 1;
			double N_v1 = child.nVisits;
			double explore = C * Math.sqrt(Math.log(N_v) / (N_v1 + EPS));
			double tieBreak = RAND.nextDouble() * EPS;
			double ucb = childValue + explore + tieBreak;

			if (ucb > bestUCB) {
				bestChild = child;
				bestUCB = ucb;
			}
		}
		return bestChild;
	}

	public double defaultPolicy() {
		// Follow the default policy and return the reward of the final state
		return state.rollOut();
	}

	void backpropagate(TreeNode nod, double value) {
		TreeNode n = nod;
		while (n != null) {
			n.nVisits++;
			n.reward += value;
			value = -value; // /WK/ negamax variant for two-player tree
			n = n.parent;
		}
	}

	public boolean isFullyExpanded() {
		for (TreeNode c : children) {
			if (c == null)
				return false;
		}
		return true;
	}

	public int mostVisitedAction() {
		// Apparently, the most visited action is preferable to the best action.
		// Why?
		int most = Integer.MIN_VALUE, mostAct = Integer.MIN_VALUE;
		boolean allEqual = true, first = true;

		for (int i = 0; i < children.length; i++) {
			TreeNode c = children[i];
			if (c != null && c.nVisits > most) {
				allEqual = first;
				most = c.nVisits;
				mostAct = state.getAction(i);
				first = false;
			}
		}

		// if the number of visits is equal for all moves, then select the move
		// with the best
		if (allEqual)
			mostAct = bestAction();
		return mostAct;
	}

	private double bestAction_Value(boolean getBestAction) {
		// TODO: In Diego Perez implementation: only based on total reward. Is
		// this correct? Why not scale by number of visits.
		double bestValue = Double.NEGATIVE_INFINITY;
		int bestAction = Integer.MIN_VALUE;
		for (int i = 0; i < children.length; i++) {
			TreeNode c = children[i];
			if (c != null) {
				// The values of the child are seen from the view of the
				// opponent
				// (always from the player to move). Therefore, his reward is my
				// loss and vice versa. We have to invert his reward.
				double val = -c.reward / c.nVisits;
				if (val > bestValue) {
					bestValue = val;
					bestAction = state.getAction(i);
				}
			}
		}
		return (getBestAction ? bestAction : bestValue);
	}

	public double bestValue() {
		return bestAction_Value(false);
	}

	public int bestAction() {
		return (int) bestAction_Value(true);
	}

	public double[] allActionValues() {
		double[] actionValues = new double[ConnectFour.COLCOUNT];
		Arrays.fill(actionValues, Double.NaN);
		int numActions = state.getNumActions();
		if (DEBUG)
			assert numActions == children.length;
		for (int i = 0; i < numActions; i++) {
			int action = state.getAction(i);
			TreeNode c = children[i];
			double value = c.reward / c.nVisits;
			actionValues[action] = value;
		}
		return actionValues;
	}

	public TreeNode find(GameState s, int maxDepth) {
		// Try to find the position in the tree (until a certain depth)
		if (depth == maxDepth)
			return null; // could not find position
		for (TreeNode c : children) {
			if (c != null) {
				if (c.state.equals(s))
					return c;
				TreeNode nod = c.find(s, maxDepth);
				if (nod != null)
					return nod;
			}
		}
		return null;
	}

}

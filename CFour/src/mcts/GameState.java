package mcts;

import guiOptions.OptionsMinimax;

import java.util.Arrays;
import java.util.Random;

import openingBook.BookSum;

import c4.AlphaBetaAgent;
import c4.ConnectFour;

// TODO: Do we really have to store the whole state-information? Is it not enough just to store the action, 
// since the state is defined by a sequence of actions?

public class GameState extends ConnectFour {
	private static final AlphaBetaAgent AB = initAlphaBetaAgent();
	private static final boolean DEBUG = true;
	private static final Random RAND = new Random();

	private int p; // player to move from this state

	/**
	 * Movelist. Possible actions from this state Use
	 * {@link ConnectFour#generateMoves(int)} for to create the move-list for
	 * the current state.
	 */
	private int[] actions;

	public GameState(int field[][]) {
		super(field); // inits fieldP1, fieldP2 and colHeight

		// Determine player to move from current position
		p = playerToMove();

		// Create action-list for current position
		this.actions = generateMoves(p, false);
	}

	public GameState(long fieldP1, long fieldP2, int[] colHeight, int p,
			int[] actions) {
		super(fieldP1, fieldP2, colHeight);

		// DEBUG-Mode: Check parameters
		if (DEBUG) {
			assert (colHeight.length == COLCOUNT);
			assert (p == PLAYER1 || p == PLAYER2);
			assert (p == playerToMove());
		}

		this.actions = actions;
		this.p = p;
	}

	// For Debug only
	private static AlphaBetaAgent initAlphaBetaAgent() {
		OptionsMinimax min = new OptionsMinimax(AlphaBetaAgent.TRANSPOSBYTES);
		AlphaBetaAgent ab = new AlphaBetaAgent(new BookSum());
		ab.resetBoard();

		ab.setTransPosSize(min.getTableIndex());
		ab.setBooks(min.useNormalBook(), min.useDeepBook(),
				min.useDeepBookDist());
		ab.setDifficulty(min.getSearchDepth());
		ab.randomizeEqualMoves(min.randomizeEqualMoves());

		// Using N-Tuple-System for move-Ordering
		// ab.setTDAgent((TDSAgent) players[2]);
		return ab;
	}

	/**
	 * Determines the move-list.
	 * 
	 * @param fieldP1
	 * @param fieldP2
	 * @param colHeight
	 */
	public GameState(long fieldP1, long fieldP2, int[] colHeight, int p) {
		super(fieldP1, fieldP2, colHeight);

		// DEBUG-Mode: Check parameters
		if (DEBUG) {
			assert (colHeight.length == COLCOUNT);
			assert (p == PLAYER1 || p == PLAYER2);
		}

		this.p = p;

		this.actions = generateMoves(p, false);
	}

	GameState getCopy(boolean copyActions) {
		int[] actions = (copyActions ? this.actions.clone() : null);
		return new GameState(fieldP1, fieldP2, colHeight.clone(), p, actions);
	}

	public double rollOut() {
		// TODO: roll-out, starting with this game-state
		// TODO: In the beginning, use Minimax as return for rollout

		// Copy the current state, in order to prevent it from getting changed.
		// The copy can be changed as desired
		GameState s = getCopy(true);
		double reward = Double.NEGATIVE_INFINITY;
		boolean gameOver = false;

		// Testwise get the real value instead of doing a random rollout
		if (DEBUG && false) {
			double value = AB.getScore(getBoard(), true);
			reward = (s.p == PLAYER1 ? value : -value);
			return reward;
		}

		// ends in a in a win/loss/draw
		while (!gameOver) {
			// if player to move can win, then the game is already over!
			if (s.hasWin(s.p)) {
				// the player to move always maximizes.
				reward = (s.p == p ? 1.0 : -1.0);
				gameOver = true;
			} else if (s.isDraw()) {
				reward = 0.0;
				gameOver = true;
			} else {
				// Select after-state randomly
				s.advance();
			}
		}

		return reward;
	}

	public int getRandomAction() {
		int n = RAND.nextInt(actions.length);
		return actions[n];
	}

	public boolean isTerminal() {
		return (hasWin(p) || isDraw());
	}

	public void advance() {
		advance(getRandomAction());
	}

	public int getNumActions() {
		return actions.length;
	}

	public int getAction(int index) {
		// Throws OutOfBoundsException if index is incorrect
		return actions[index];
	}

	public void advance(int action) {
		if (DEBUG)
			assert p == (countPieces() % 2 == 0 ? PLAYER1 : PLAYER2);
		// Place the piece on the board
		// this implicitly changes fieldP1, fieldP2 and colHeight
		putPiece(p, action);

		// swap player to move
		p = (p == PLAYER1 ? PLAYER2 : PLAYER1);

		// We have to generate a new action-list for this new state
		actions = generateMoves(p, false);
	}
	
	@Override
	public String toString() {
		return toString(getBoard());
	}
	
	@Override
	public boolean equals(Object o) {
		GameState s = (GameState) o;
		boolean ret = (s.fieldP1 == fieldP1 && s.fieldP2 == fieldP2);
		if(DEBUG && ret) {
			assert (Arrays.equals(s.colHeight, colHeight));
			assert (s.p == p);
			assert (Arrays.equals(s.actions, actions));
		}
		// For two positions to be equal, only the boards have to be compared
		return ret;
	}

}

package nTupleTD;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.Semaphore;

import c4.Agent;
import c4.AlphaBetaAgent;
import c4.ConnectFour;
import competition.Progress;

/**
 * 
 * The TD-Learning for Connect Four. The value-function is modeled with a
 * N-Tuple-System. It is possible to train whole games or to stop a game after x
 * moves. In this case a Minimax-Agent (referee) is needed to get the real
 * rewards.
 * 
 * @author Markus Thill
 */
public class TDSAgent extends ConnectFour implements Agent, Progress,
		Serializable {

	private static final long serialVersionUID = 1095764704484352593L;

	private Random rand = new Random();

	// Binary Semaphore, to prevent multiple access (e.g. by parallel threads)
	private Semaphore mutex = new Semaphore(1);

	// Value-Function using a N-Tuple-System
	public ValueFuncC4 m_Net;

	// All params needed to train the Net
	private TDParams td;

	// Prob. for explorative Moves
	private double m_epsilon;

	// Cur. number of trained games
	private int m_GameNum;

	// Needed, if rewards shall be given before game is over
	private Agent referee = null;

	private AgentState state = AgentState.INITIALIZED;

	/**
	 * Creates a new TD-Agent
	 * 
	 * @param tdPar
	 *            All needed Params for the TD-Agent
	 */
	public TDSAgent(TDParams tdPar) {
		super();
		reinitTDSAgent(tdPar);
	}

	/**
	 * Creates a new TD-Agent
	 * 
	 * @param tdPar
	 *            All needed Params for the TD-Agent
	 * @param referee
	 *            typ. a Minimax-Agent
	 */
	public TDSAgent(TDParams tdPar, Agent referee) {
		super();
		reinitTDSAgent(tdPar);
		this.referee = referee;
	}

	/**
	 * Reset all Elements of this TD-Agent
	 * 
	 * @param tdPar
	 */
	public void reinitTDSAgent(TDParams tdPar) {
		state = AgentState.INITIALIZED;
		td = tdPar;
		resetBoard();
		createNet();
		setTDParams(tdPar);
	}

	/**
	 * Init the Value-Function with the N-Tuple-System
	 */
	private void createNet() {
		m_Net = new ValueFuncC4(td);
	}

	/**
	 * Recursive look-ahead algorithm (negamax-variant of the minimax-algorithm
	 * The scores at the leafs at fetched from the N-Tuple-System. For large
	 * depths this method converges to a perfect value-function. But large
	 * depths are not recommended, because the number of nodes to be evaluated
	 * increases exponentially.
	 * 
	 * @param player
	 *            current player, to make next move
	 * @param restDepth
	 *            depth to look ahead
	 * @return value for the current board
	 */
	private double getScore(int player, int restDepth, long zobr) {
		// Check, if Draw
		if (isDraw())
			return 0;

		// Get Value from environment
		int countPieces = countPieces();
		if (countPieces == td.stopAfterMoves) //
			return referee.getScore(getBoard(), true);

		// Get Value from N-Tuple-System
		if (restDepth <= 0) {
			double value = m_Net.getValue(player, zobr, new long[] { fieldP1,
					fieldP2 });
			return value;
		}

		double fak = (player == PLAYER1 ? 1.0 : -1.0);

		// Check, if current-Player can win with this move
		if (hasWin(player))
			return fak;

		int otherPlayer = (player == PLAYER1 ? PLAYER2 : PLAYER1);

		// get all possible legal moves
		int[] mv = generateMoves(player, true);
		double bestVal = Double.NEGATIVE_INFINITY, value;

		// Try all Moves and find best one
		for (int i = 0; mv[i] != -1; i++) {
			putPiece(player, mv[i]);
			long newZobr = zobr
					^ rnd[player == PLAYER1 ? 0 : 1][mv[i] * 6 - 1
							+ colHeight[mv[i]]];
			// recursion
			value = fak * getScore(otherPlayer, restDepth - 1, newZobr);

			// Check if this move is better
			if (value > bestVal)
				bestVal = value;
			removePiece(player, mv[i]);
		}
		return bestVal * fak;
	}

	/**
	 * Based on the current board, try to find the best after-state and return
	 * the move that leads to this state.
	 * 
	 * @param player
	 *            Find best Move for this player
	 * @param random
	 *            true: random selection will occur with a certain probability,
	 *            false: random selection turned off
	 * @param bestMove
	 *            The resulting best Move for player (array with one element)
	 * @return true: if a random move was made
	 */
	private boolean getBestMove(int player, boolean random, int[] bestMove,
			long zobr) {
		boolean randomSelect = false;

		// determine if random move shall be made
		if (random) {
			if (rand.nextDouble() < m_epsilon) {
				randomSelect = true;
			}
		}

		// get all possible legal moves
		int[] mv = generateMoves(player, true);
		if (randomSelect) {
			int i;
			for (i = 0; mv[i] != -1; i++)
				;
			bestMove[0] = mv[rand.nextInt(i)];
		} else {
			bestMove[0] = -1;
			double bestVal = Double.NEGATIVE_INFINITY, score;
			double fak = (player == PLAYER1 ? 1.0 : -1.0);

			// Try all Moves and find best one
			for (int i = 0; mv[i] != -1; i++) {

				if (canWin(player, mv[i])) {
					bestVal = Double.POSITIVE_INFINITY;
					bestMove[0] = mv[i];
					continue;
				}

				putPiece(player, mv[i]);

				// get other player
				int otherPlayer = (player == PLAYER1 ? PLAYER2 : PLAYER1);

				// new
				long newZobr = -1L;
				if (td.useHashIntern) {
					int rndIndex = mv[i] * 6 - 1 + colHeight[mv[i]];
					int rndPlayer = player == PLAYER1 ? 0 : 1;
					newZobr = zobr ^ rnd[rndPlayer][rndIndex];
				}
				score = fak * getScore(otherPlayer, td.nPlyLookAhead, newZobr);

				// Throw exception when score is -infinity. Happens normally
				// when alpha is choosen too large
				if (score <= Double.NEGATIVE_INFINITY || score == Double.NaN)
					throw new UnsupportedOperationException(
							"N-Tuple-System returned a INFINITY-value. Consider choosing a smaller alpha-range (alpha_init and alpha_final).");

				// Check if this move is better
				if (score > bestVal) {
					bestVal = score;
					bestMove[0] = mv[i];
				}
				removePiece(player, mv[i]);
			}
		}
		return randomSelect;
	}

	/**
	 * @param player
	 *            Player(1,2) which makes the next move. If player=2 then a
	 *            random piece for player 1 will be set
	 * @return nothing yet
	 */
	public boolean trainNet(int player) {
		double reward = 0.0; // Reward from environment
		boolean randomMove; // Set, if random Move was selected by method
		boolean finished = false; // Set, if Win or Tie is found
		int bestMove[] = new int[1];
		long lastBoard[] = new long[2];

		long zobr = 0L, lastZobr = 0L;

		resetBoard();

		// Init elig-traces
		// Empty board
		// Zobrist key is zero for empty board
		// For Current player for the empty board
		if (td.lambda != 0.0)
			m_Net.resetElig(new long[] { 0L, 0L }, zobr, ConnectFour.PLAYER1); // reset
																				// elig
																				// for
																				// empty
																				// board

		if (player == PLAYER2) {
			// set a random "X" in the empty table:
			int col = rand.nextInt(COLCOUNT);
			putPiece(PLAYER1, col);
		}

		while (!finished) {
			randomMove = getBestMove(player, true, bestMove, zobr);

			// Check if cur. player can win with this move
			if (canWin(player, bestMove[0])) {
				// More distant losses are better than closer ones
				// More nearer wins are also better
				double distance = (double) countPieces() / 100.0;

				// +1 for white win, -1 for black win
				reward = (player == PLAYER1 ? 1.0 - distance : -1.0 + distance);
				finished = true;
			}

			// Save current Board;
			lastBoard[0] = fieldP1;
			lastBoard[1] = fieldP2;
			lastZobr = zobr;

			// make random or best move
			putPiece(player, bestMove[0]);
			int rndPlayer = player == PLAYER1 ? 0 : 1;
			int rndIndex = bestMove[0] * 6 - 1 + colHeight[bestMove[0]];
			zobr ^= rnd[rndPlayer][rndIndex];

			// Give premature reward if wanted
			if (countPieces() == td.stopAfterMoves && !finished)
				if (referee != null) {
					finished = true;
					reward = referee.getScore(getBoard(), true);
				}

			// Check for Draw
			if (isDraw() && !finished) {
				reward = 0.0;
				finished = true;
			}

			// Update weights if no random move was made or if the game is over
			long curBoard[] = new long[] { fieldP1, fieldP2 };
			if (!randomMove || finished || td.epsRandUpdate) {

				if (td.useHashIntern)
					m_Net.updateWeights(player, lastZobr, zobr, lastBoard,
							curBoard, finished, reward);
				else
					m_Net.updateWeights(player, -1L, -1L, lastBoard, curBoard,
							finished, reward);

				// Test: Learn Value-Function for inverted board
				// if(false && m_GameNum < 200000)
				// {
				// double rewardNew = -reward;
				// long curBoardNew[] = new long[] { fieldP2, fieldP1 };
				// long lastBoardNew[] = new long[] { lastBoard[1], lastBoard[0]
				// };
				// int playerNew = (player == PLAYER1 ? PLAYER2 : PLAYER1);
				// m_Net.updateWeights(playerNew, -1L, -1L, lastBoardNew,
				// curBoardNew,
				// finished, rewardNew);
				// }
			}

			// swap players
			player = (player == PLAYER1 ? PLAYER2 : PLAYER1);

			boolean resetEligOnRandomMove = td.resetEligOnRandomMove;

			// Possibility to update value-function when
			// random moves are performed
			if (!randomMove || !resetEligOnRandomMove) {
				// Update elig-traces
				// curBoard: state s_{t+1}
				// zobr: the zobrist-key for the current state s_{t+1}
				// player: player to move for the current state s_{t+1}. The
				// function upfateWeights also uses the opposite player for the
				// V(s_{t+1}).
				// TODO: scale Elig on random move, or not???
				if (td.lambda != 0.0)
					m_Net.updateElig(curBoard, zobr, player, true);
			} else if (td.lambda != 0.0) {
				// if we made a random move, we maybe have to reset the
				// eligibility
				// traces. Simply set the eligibity-tace vector to the gradient
				// of
				// the new state (s_{t+1})
				m_Net.resetElig(curBoard, zobr, player);
			}

		}

		// update alpha and epsilon
		updateParameters();

		m_GameNum++;
		state = AgentState.TRAINED;
		return false;
	}

	public int countActiveEligTraces() {
		return m_Net.countActiveEligTraces();
	}

	/**
	 * adjust alpha and epsilon
	 */
	public void updateParameters() {
		m_Net.updateAlpha(); // adjust learn param ALPHA
		updateEpsilon();

	}

	/**
	 * adjust epsilon
	 */
	private void updateEpsilon() {
		double a0 = td.epsilonInit;
		double a1 = td.epsilonFinal;
		// 04.11.2013
		// replaced td.trainGameNum with Constant 10 Mio. in order to get the
		// same behaviour for epsilon when only 5 mio. training-games are used
		int trainGameNum = (td.trainGameNum < 1000000 ? 5000000
				: td.trainGameNum);
		switch (td.epsilonMethod) {
		case 0: // exponential
			double epsilonChangeRatio = Math.pow(a1 / a0, 1.0 / trainGameNum);
			m_epsilon *= epsilonChangeRatio;
			break;
		case 1: // linear
			m_epsilon = (a1 - a0) / (double) trainGameNum * (double) m_GameNum
					+ a0;
			break;
		case 2: // Tanh
			double m = td.epsilonSlope; // Steilheit
			double wp = td.epsilonChangeParam; // Wendepunkt
			double c = wp / trainGameNum * m;
			double x = (double) m_GameNum / (trainGameNum) * m;
			double fx = (a0 - a1) / 2.0 * (1 - Math.tanh(x - c)) + a1;
			m_epsilon = fx;
			break;
		case 3: // Sprung
			m_epsilon = (m_GameNum < td.epsilonChangeParam ? a0 : a1);
			break;
		}
	}

	/**
	 * Set new learning params
	 * 
	 * @param tdPar
	 */
	public void setTDParams(TDParams tdPar) {
		m_epsilon = tdPar.epsilonInit;
		// m_Net.setTDParams(tdPar);
		m_GameNum = 0;
	}

	/**
	 * @return current learning-rate alpha
	 */
	public double getAlpha() {
		return m_Net.getAlpha();
	}

	/**
	 * @return current prob. for explorative moves
	 */
	public double getEpsilon() {
		return m_epsilon;
	}

	/**
	 * @return all relevant Params of the TD-Agent
	 */
	public TDParams getTDParams() {
		return td;
	}
	
	public WeightSubSet[][] getWeightSubset() {
		return m_Net.getWeightSubSet();
	}

	public String getCommonLR() {
		return m_Net.getCommonLR();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see c4.Agent#getBestMove(int[][])
	 */
	@Override
	public int getBestMove(int[][] table) {
		semOpDown();
		setBoard(table);

		int player = (countPieces() % 2 == 0 ? PLAYER1 : PLAYER2);
		int bestMove[] = new int[1];
		getBestMove(player, false, bestMove,
				AlphaBetaAgent.toZobrist(fieldP1, fieldP2));
		semOpUp();
		return bestMove[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see c4.Agent#getScore(int[][], boolean)
	 */
	@Override
	public double getScore(int[][] table, boolean useSigmoid) {
		semOpDown();
		setBoard(table);
		int player = (countPieces() % 2 == 0 ? PLAYER1 : PLAYER2);
		double score = 0;
		if (hasWin(player))
			score = player == PLAYER1 ? 1.0 : -1.0;
		else {
			long zobr = -1L;
			if (td.useHashExtern)
				zobr = AlphaBetaAgent.toZobrist(fieldP1, fieldP2);
			score = getScore(player, td.nPlyLookAhead, zobr);
		}
		semOpUp();
		return score;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see c4.Agent#getNextVTable(int[][], boolean)
	 */
	@Override
	public double[] getNextVTable(int[][] table, boolean useSigmoid) {
		semOpDown();
		setBoard(table);
		double[] values = new double[7];
		int player = (countPieces() % 2 == 0 ? PLAYER1 : PLAYER2);
		int x;
		for (x = 0; x < COLCOUNT; x++) {
			if (colHeight[x] < ROWCOUNT) {
				if (canWin(player, x)) {
					values[x] = (player == PLAYER1 ? 1.0 : -1.0);
					continue;
				}
				putPiece(player, x);

				// get other player
				int otherPlayer = (player == PLAYER1 ? PLAYER2 : PLAYER1);
				long zobr = -1L;
				if (td.useHashExtern)
					zobr = AlphaBetaAgent.toZobrist(fieldP1, fieldP2);
				values[x] = getScore(otherPlayer, td.nPlyLookAhead, zobr);
				removePiece(player, x);
			} else
				values[x] = Double.NaN;
		}
		semOpUp();
		return values;
	}

	/**
	 * Get MoveList for Move Ordering with N-Tuple-System in Alpha-Beta-Agent
	 * 
	 * @param f1
	 * @param f2
	 * @return MoveList
	 */
	public int[] getBestMoveList(long f1, long f2) {
		// double vals[] = getNextVTable();
		setBoard(f1, f2);
		double[] values = new double[7];
		int player = (countPieces() % 2 == 0 ? PLAYER1 : PLAYER2);
		int x;
		for (x = 0; x < COLCOUNT; x++) {
			if (colHeight[x] < ROWCOUNT) {
				putPiece(player, x);
				// get other player
				int otherPlayer = (player == PLAYER1 ? PLAYER2 : PLAYER1);
				long zobr = -1L;
				// if (td.useHashExtern)
				// zobr = AlphaBetaAgent.toZobrist(fieldP1, fieldP2);
				values[x] = getScore(otherPlayer, 0, zobr);

				removePiece(player, x);
			} else
				values[x] = Double.NEGATIVE_INFINITY;
		}

		int moves[] = new int[8];
		double val;
		int j = 0, index = 0;
		do {
			val = Double.NEGATIVE_INFINITY;
			for (x = 6; x >= 0; x--) {
				if (values[x] > val) {
					val = values[x];
					index = x;
				}
			}
			values[index] = Double.NEGATIVE_INFINITY;
			moves[j++] = index;
		} while (val > Double.NEGATIVE_INFINITY);
		moves[j - 1] = -1;
		return moves;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see c4.Agent#getName()
	 */
	@Override
	public String getName() {
		return "TDS-Agent";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Progress#getProgress()
	 */
	@Override
	public int getProgress() {
		return (100 * m_GameNum) / td.trainGameNum;
	}

	@Override
	public String getStatusMessage() {
		String str = "Training TDS Agent...";
		return str;
	}

	@Override
	public void semOpDown() {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void semOpUp() {
		mutex.release();
	}

	@Override
	public AgentState getAgentState() {
		return state;
	}

}

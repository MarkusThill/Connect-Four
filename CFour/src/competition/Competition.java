package competition;

import c4.AlphaBetaAgent;
import c4.ConnectFour;
import c4.Agent;

/**
 * Allows different types of competitions between two Connect-Four-Agents
 * implementing the interface Agent.
 * 
 * @author Markus Thill
 */
public class Competition implements Progress {

	// Field containing both agents
	private Agent[] agent = new Agent[2];

	// Referee for the "real" values
	private final AlphaBetaAgent referee;

	// Game-environment
	private ConnectFour c4 = new ConnectFour();

	// Progress for the actions
	private int progress;

	// needed for Progress
	private boolean isMultiTest = false;

	/**
	 * @param X
	 *            First Agent
	 * @param O
	 *            Second Agent
	 * @param referee
	 *            A perfect playing AB-Agent. If this parameter is null, then
	 *            the referee is initialized in the constructor
	 */

	public Competition(Agent X, Agent O, AlphaBetaAgent referee) {
		// if (referee == null)
		// this.referee = new AlphaBetaAgent(refereeBooks);
		// else
		this.referee = referee;
		agent[0] = X;
		agent[1] = O;
	}

	/**
	 * Set the initial board for the next competition. If not changed, this
	 * board will be used for all competitions.
	 * 
	 * @param board
	 */
	public void setInitialBoard(int[][] board) {
		c4.setBoard(board);
	}

	/**
	 * performs a multiple number of competitions between the two agents.
	 * 
	 * @param compNum
	 *            Number of Competitions
	 * @param pieceNum
	 *            Stop game after pieceNum pieces are on the board an get the
	 *            result by the referee.
	 * @param boards
	 *            boards for every single competition. If null, an empty board
	 *            will be taken as initial position for every competition.
	 * @param swapPlayers
	 *            swap players after each competition. Each agent will start the
	 *            game for one board once. The number of competitions will
	 *            double in this case
	 * @param getValues
	 *            choose, if the values of the agents and the referee shall be
	 *            logged for each move made. Makes a competition take longer.
	 * @return an Object containing an Overview and every single game.
	 */
	private ResultCompMulti multiCompete(int compNum, int pieceNum,
			int boards[][][], boolean swapPlayers, boolean getValues) {

		isMultiTest = true;

		if (swapPlayers)
			compNum *= 2;

		ResultCompMulti mcr = new ResultCompMulti();
		// Save Board
		int[][] board = c4.getBoard();
		ResultCompSingle[] compResults = new ResultCompSingle[compNum];

		for (int i = 0; i < compNum; i++) {
			// set Progress
			progress = ((i + 1) * 100) / compNum;
			if (boards == null)
				c4.setBoard(board);
			else {
				int index = (swapPlayers ? (int) i / 2 : i);
				c4.setBoard(boards[index]);
			}
			compResults[i] = compete(pieceNum, getValues, false);

			// Check, who won
			int res = compResults[i].winner;
			if (!swapPlayers || i % 2 == 0) {
				if (res == 0)
					mcr.numWonX++;
				else if (res == 1)
					mcr.numWonO++;
				else
					mcr.numDraw++;
			} else {
				if (res == 0)
					mcr.numWonO++;
				else if (res == 1)
					mcr.numWonX++;
				else
					mcr.numDraw++;
			}

			int exp = compResults[i].expectedWinner;
			if (!swapPlayers || i % 2 == 0) {
				if (exp == 0)
					mcr.numExpectedWonX++;
				else if (exp == 1)
					mcr.numExpectedWonO++;
				else
					mcr.numExpectedDraw++;
			} else {
				if (exp == 0)
					mcr.numExpectedWonO++;
				else if (exp == 1)
					mcr.numExpectedWonX++;
				else
					mcr.numExpectedDraw++;
			}

			mcr.totalTime += compResults[i].time;
			mcr.avgMovesNum += compResults[i].numMovesMade;

			// Swap Players
			if (swapPlayers) {
				Agent tmp = agent[0];
				agent[0] = agent[1];
				agent[1] = tmp;
			}
		}

		mcr.avgMovesNum /= (float) compNum;
		mcr.numGames = compNum;
		mcr.singleResults = compResults;
		isMultiTest = false;
		return mcr;
	}

	/**
	 * Multi-Competition from the same initial-board
	 * 
	 * @param compNum
	 *            Number of Competitions
	 * @param swapPlayers
	 *            swap players after each competition. Each agent will start the
	 *            game for one board once. The number of competitions will
	 *            double in this case
	 * @param getValues
	 *            choose, if the values of the agents and the referee shall be
	 *            logged for each move made. Makes a competition take longer.
	 * @return Object containing an Overview and every single game.
	 */
	public ResultCompMulti multiCompete(int compNum, boolean swapPlayers,
			boolean getValues) {
		return multiCompete(compNum, 100, null, swapPlayers, getValues);
	}

	/**
	 * @param compNum
	 *            Number of Competitions
	 * @param pieceNum
	 *            Stop game after pieceNum pieces are on the board an get the
	 *            result by the referee.
	 * @param swapPlayers
	 *            swap players after each competition. Each agent will start the
	 *            game for one board once. The number of competitions will
	 *            double in this case
	 * @param getValues
	 *            choose, if the values of the agents and the referee shall be
	 *            logged for each move made. Makes a competition take longer.
	 * @return an Object containing an Overview and every single game.
	 */
	public ResultCompMulti multiCompete(int compNum, int pieceNum,
			boolean swapPlayers, boolean getValues) {
		return multiCompete(compNum, pieceNum, null, swapPlayers, getValues);
	}

	/**
	 * @param boards
	 *            boards for every single competition.
	 * @param swapPlayers
	 *            swap players after each competition. Each agent will start the
	 *            game for one board once. The number of competitions will
	 *            double in this case
	 * @param getValues
	 *            choose, if the values of the agents and the referee shall be
	 *            logged for each move made. Makes a competition take longer.
	 * @return an Object containing an Overview and every single game.
	 */
	public ResultCompMulti multiCompete(int boards[][][], boolean swapPlayers,
			boolean getValues) {
		return multiCompete(boards.length, 100, boards, swapPlayers, getValues);
	}

	/**
	 * @param pieceNum
	 *            Stop game after pieceNum pieces are on the board an get the
	 *            result by the referee.
	 * @param boards
	 *            boards for every single competition.
	 * @param swapPlayers
	 *            swap players after each competition. Each agent will start the
	 *            game for one board once. The number of competitions will
	 *            double in this case
	 * @param getValues
	 *            choose, if the values of the agents and the referee shall be
	 *            logged for each move made. Makes a competition take longer.
	 * @return an Object containing an Overview and every single game.
	 */
	public ResultCompMulti multiCompete(int pieceNum, int boards[][][],
			boolean swapPlayers, boolean getValues) {
		return multiCompete(boards.length, pieceNum, boards, swapPlayers,
				getValues);
	}

	/**
	 * Play single Game with starting Position in c4
	 * 
	 * @param getValues
	 *            choose, if the values of the agents and the referee shall be
	 *            logged for each move made. Makes a competition take longer.
	 * @return an Object containing the results of this single match.
	 */
	public ResultCompSingle compete(boolean getValues,
			boolean stopByABWinEvaluation) {
		return compete(100, getValues, stopByABWinEvaluation);
	}

	/**
	 * Like compete. But stop game, if pieceNum pieces are placed on the board
	 * 
	 * @param pieceNum
	 *            Stop game after pieceNum pieces are on the board an get the
	 *            result by the referee.
	 * @param getValues
	 *            choose, if the values of the agents and the referee shall be
	 *            logged for each move made. Makes a competition take longer.
	 * @return an Object containing the results of this single match.
	 */
	public ResultCompSingle compete(int pieceNum, boolean getValues,
			boolean stopByABWinEvaluation) {
		long zstStart = System.currentTimeMillis();

		ResultCompSingle cr = new ResultCompSingle();

		// Select the starting Player
		int curPlayer = c4.countPieces() % 2;
		cr.startingPlayer = curPlayer;

		// init Moves-List
		int[] moves = new int[42];

		// Game shoulden't be over at the beginning
		boolean gameOver = !(c4.countPieces() < 42);

		// Init Winner
		int winner = -1; // -1 for draw
		double val = referee.getScore(c4.getBoard(), true);
		if (val > 0)
			cr.expectedWinner = 0;
		else if (val < 0)
			cr.expectedWinner = 1;
		else
			cr.expectedWinner = -1;

		// List of Value-Boards for each Move
		double[][][] values = null;
		if (getValues) {
			values = new double[42][2][];
			cr.vTables = new double[42][2][];
		}

		// Play!!!!
		int i;
		for (i = 0; !gameOver; i++) {

			// Progress
			if (!isMultiTest)
				progress = ((i + 1) * 100) / pieceNum;

			// get current Board
			int[][] board = c4.getBoard();
			cr.boards[i] = board;

			// Get Value-Tables
			if (getValues) {
				values[i][0] = referee.getNextVTable(board, true);
				cr.vTables[i][0] = values[i][0];

				values[i][1] = agent[curPlayer].getNextVTable(board, true);
				cr.vTables[i][1] = agent[curPlayer].getNextVTable(board, true);
			}

			// Get best Move
			int x = agent[curPlayer].getBestMove(board);
			moves[i] = x;

			boolean isABAgent = agent[curPlayer].getClass().equals(AlphaBetaAgent.class);
			if (stopByABWinEvaluation
					&& isABAgent) {
				int sgn = (curPlayer == 0 ? 1 : -1);
				if (agent[curPlayer].getScore(board, true) * sgn > 0.0) {
					gameOver = true;
					winner = curPlayer;
				}
			}

			// Check for Win
			if (c4.canWin(curPlayer + 1, x)) {
				gameOver = true;
				winner = curPlayer;
			}
			c4.putPiece(curPlayer + 1, x);

			// Check for Draw
			if (c4.isDraw())
				gameOver = true;
			curPlayer = 1 - curPlayer;

			if (c4.countPieces() >= pieceNum && !gameOver) {
				gameOver = true;
				double actualVal = referee.getScore(c4.getBoard(), true);
				if (actualVal > 0)
					winner = 0;
				else if (actualVal < 0)
					winner = 1;
			}
		}

		cr.agents[0] = agent[0].getName() + " (X)";
		cr.agents[1] = agent[1].getName() + " (O)";

		// Winner
		cr.winner = winner;

		// Moves
		cr.moves = moves;
		cr.numMovesMade = i;

		// Time for Competition
		long zstStop = System.currentTimeMillis();
		cr.time = zstStop - zstStart;

		return cr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Progress#getProgress()
	 */
	@Override
	public int getProgress() {
		return progress;
	}

	@Override
	public String getStatusMessage() {
		String str = "Running Competition ...";
		return str;
	}
}

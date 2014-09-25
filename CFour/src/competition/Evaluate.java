package competition;

import java.text.DecimalFormat;

import nTupleTD.TDParams;

import c4.Agent;
import c4.AlphaBetaAgent;
import c4.ConnectFour;

/**
 * Allows a simple Evaluation of a TD-agent Allows a quick evaluation of the
 * selected TD-agent. The TD-agent will play a certain number of matches against
 * a perfect-playing Minimax-agent. Currently all boards with zero, one and two
 * pieces will be used as initial boards for these matches. The TD-agent will
 * always play for the theoretical winner (X = yellow or O = red). If the board
 * leads to a draw, the TD-agent will play both players once. After each match
 * it is checked if the TD-agent could reach the theoretical result.<br>
 * 
 * After all these boards are tested, the most important part of the evaluation
 * will start. The TD-Agent will still play against a perfect Alpha-Beta-agent
 * from an empty board. But this time the Alpha-Beta-Agent will make more random
 * moves, so that every match will be different. Mistakes of the TD-Agent will
 * still be punished directly so that he loses the game. The Alpha-Beta-Agent
 * will also prevent direct losses in the opening-phase of the game so that the
 * TD-Agent has to hold steady for a while and make correct moves. After the
 * opening phase (currently 12 pieces) the Alpha-Beta-Agent will seek for the
 * most distant loss so that the TD-agent has to play perfect to end to win the
 * game. Currently 50 of these matches are performed between both agents and
 * rated higher than the previous matches, because these kind of matches are
 * more realistic.
 * 
 * All matches are used to determine the score. The score lies in the range 0
 * (TD-agent NEVER reached the possible theoretical best state) and +1 (TD-agent
 * ALWAYS reached the possible theoretical best state).
 * 
 * @author Markus Thill
 * 
 */
public class Evaluate implements Progress {

	// Number of boards
	// 0 Pieces -> 1
	// 1 Piece -> 7
	// For 2Pieces: Don't eval symmetric positions, only those with X in the
	// middle
	// column, because these are more likely
	// 2 Pieces -> 4*7 = 28
	// ------------------------
	// ____36_____
	private static final int NUMBOARDS = 36;
	private static final boolean DEBG = false;

	// Games against completely random agent from empty board
	//private static final int GAMESAGAINSTRANDOMAB = 200; 
	private int boards[/* board */][][];
	private int winners[];
	private int maxPoints;
	private int progress;
	private long start, stop;

	public Evaluate(AlphaBetaAgent ab) {

		if (DEBG)
			System.out.println("[Init Evaluation]");

		long zstStart = System.currentTimeMillis();
		
		boards = new int[NUMBOARDS][][];
		winners = new int[NUMBOARDS];

		ConnectFour c4 = new ConnectFour();
		c4.resetBoard();

		int winner;
		double score;

		// Empty board
		boards[0] = c4.getBoard();
		winners[0] = ConnectFour.PLAYER1;

		// Boards with one piece
		for (int i = 0; i < 7; i++) {
			c4.resetBoard();
			c4.putPiece(i);
			boards[i + 1] = c4.getBoard();
			score = ab.getScore(boards[i + 1], true);
			if (score < 0)
				winner = ConnectFour.PLAYER2;
			else if (score > 0)
				winner = ConnectFour.PLAYER1;
			else
				winner = 0;
			winners[i + 1] = winner;
		}

		// Boards with 2 pieces
		for (int i = 0, k = 8; i <= 3; i++) {
			for (int j = 0; j < 7; j++) {
				c4.resetBoard();
				c4.putPiece(i);
				c4.putPiece(j);

				boards[k] = c4.getBoard();
				score = ab.getScore(boards[k], true);
				if (score < 0)
					winner = ConnectFour.PLAYER2;
				else if (score > 0)
					winner = ConnectFour.PLAYER1;
				else
					winner = 0;
				winners[k++] = winner;
			}

		}

		// Calc max. num of points that can be reached by TD-Agent
		// Loss: 0 Points
		// Draw (should be Win): 1 Point
		// Win / Draw(should be Draw): 2 Points
		maxPoints = winners.length * 2;

		if (DEBG) {
			// Debug
			for (int i = 0; i < boards.length; i++) {
				ConnectFour.printBoard(boards[i]);
				System.out.println("winner: " + winners[i] + "\n");
			}

			// Stop time
			long zstStop = System.currentTimeMillis();
			long time = zstStop - zstStart;
			System.out.println("Time needed for Init: " + time + "ms");
			System.out.println("[Evaluation Init complete]");
		}
	}

	public double[] getScore(Agent td, AlphaBetaAgent ab, TDParams tdPar) {
		start = System.currentTimeMillis();
		
		boolean calc012 = tdPar.evaluate012; 
		boolean calc50Games = tdPar.evaluateAgent;
		int numEvaluationGames = tdPar.numEvaluationMatches;
		
		double score = 0.0, score1 = 0.0;
		Competition compX = new Competition(td, ab, ab);
		Competition compO = new Competition(ab, td, ab);
		ResultCompSingle rcs;
		if (calc012) {
			for (int i = 0; i < boards.length; i++) {
				progress = (i * 100) / (boards.length + numEvaluationGames);
				if (DEBG)
					System.out.println("Game " + i + " / " + boards.length);
				if (winners[i] == 0) {
					// Two Games for Draw (X and O)
					compX.setInitialBoard(boards[i]);
					compO.setInitialBoard(boards[i]);

					// Play as Player-X
					rcs = compX.compete(tdPar.stopAfterMoves, false, true);
					if (rcs.expectedWinner != winners[i] - 1)
						throw new RuntimeException(
								"\nExpected Winner is wrong\n"
										+ ConnectFour.toString(boards[i])
										+ "\n" + "winners[i]: " + winners[i]
										+ "\n" + "expected: "
										+ rcs.expectedWinner);
					if (rcs.winner + 1 == 0)
						score++;

					// Play as Player-O
					rcs = compO.compete(tdPar.stopAfterMoves, false, true);
					if (rcs.expectedWinner != winners[i] - 1)
						throw new RuntimeException(
								"\nExpected Winner is wrong\n"
										+ ConnectFour.toString(boards[i])
										+ "\n" + "winners[i]: " + winners[i]
										+ "\n" + "expected: "
										+ rcs.expectedWinner);
					if (rcs.winner + 1 == 0)
						score++;

				} else if (winners[i] == ConnectFour.PLAYER1) {
					compX.setInitialBoard(boards[i]);

					// Play as Player-X
					rcs = compX.compete(tdPar.stopAfterMoves, false, true);
					if (rcs.expectedWinner != winners[i] - 1)
						throw new RuntimeException(
								"\nExpected Winner is wrong\n"
										+ ConnectFour.toString(boards[i])
										+ "\n" + "winners[i]: " + winners[i]
										+ "\n" + "expected: "
										+ rcs.expectedWinner);
					if (rcs.winner + 1 == ConnectFour.PLAYER1)
						score += 2;

				} else if (winners[i] == ConnectFour.PLAYER2) {
					compO.setInitialBoard(boards[i]);
					// Play as Player-O
					rcs = compO.compete(tdPar.stopAfterMoves, false, true);
					if (rcs.expectedWinner != winners[i] - 1)
						throw new RuntimeException(
								"\nExpected Winner is wrong\n"
										+ ConnectFour.toString(boards[i])
										+ "\n" + "winners[i]: " + winners[i]
										+ "\n" + "expected: "
										+ rcs.expectedWinner);
					if (rcs.winner + 1 == ConnectFour.PLAYER2)
						score += 2;
				}
			}
		}

		// Always take the empty board as initial board. The AlphaBetaAgent will
		// play very randomly only preventing losses in a near distance. This
		// allows a lot of different games. Nonetheless will the agent punish
		// wrong moves of the TD-Agent and win/draw the game if possible.
		int additionalPoints = 0;
		int numDraw = 0;
		if (calc50Games) {
			ab.randomizeLosses(true);
			for (int i = 0; i < numEvaluationGames; i++) {
				progress = ((i + boards.length + 1) * 100)
						/ (boards.length + numEvaluationGames);
				additionalPoints += 2;

				compX.setInitialBoard(boards[0]); // Empty board

				// Play as Player-X
				rcs = compX.compete(tdPar.stopAfterMoves, false, true);
				if (rcs.winner + 1 == ConnectFour.PLAYER1)
					score1 += 2;

				else if (rcs.winner + 1 == 0) {
					score1++;
					numDraw++;
				} 
				//if (rcs.winner + 1 == ConnectFour.PLAYER1)
					 //rcs.printResult("Debug", true);
				//	System.out.println(rcs.numMovesMade +"");

				if (DEBG)
					rcs.printResult("Debug", true);

			}
			ab.randomizeLosses(false);
		}
		stop = System.currentTimeMillis();

		return new double[] { score / maxPoints, score1 / additionalPoints,
				(double) numDraw / numEvaluationGames };
	}

	public String getTime() {
		long time = (stop - start) / 1000; // in sec
		return "Evaluation-time: " + time + "s";
	}

	public String getScoreStr(Agent td, AlphaBetaAgent ab, TDParams tdPar) {
		DecimalFormat df = new DecimalFormat("+0.000");
		double[] score = getScore(td, ab, tdPar);
		String str = new String();
		if (tdPar.evaluate012)
			str += "Reached score for all positions with 0,1 and 2 pieces: "
					+ df.format(score[0]) + " (min:0 ; max: 1.0)\n";
		if (tdPar.evaluateAgent)
			str += "Reached score for  " + tdPar.numEvaluationMatches
					+ " games from empty board: " + df.format(score[1])
					+ " (min:0 ; max: 1.0)\n" + "Draw: " + score[2] + "\n";
		return str;
	}
	
	/**
	 * @return All boards with 0,1,2 Stones
	 */
	public int[][][] getBoards() {
		return boards;
	}

	@Override
	public int getProgress() {
		return progress;
	}

	@Override
	public String getStatusMessage() {
		return "Evaluating ... " + progress + "%";
	}
}

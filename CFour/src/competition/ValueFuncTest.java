package competition;

import java.util.Random;

import openingBook.BookSum;
import c4.AlphaBetaAgent;
import c4.ConnectFour;
import c4.Agent;
import c4.RandomAgent;

/**
 * Provides Methods for testing the Value-Function of an Agent. The values for
 * single or multiple boards can be compared with the real values. Furthermore
 * the agent can be tested in finding the optimal move for specific boards where
 * only one "best-move" is existing.
 * 
 * @author Markus Thill
 */
public class ValueFuncTest implements Progress {

	private Random rand = new Random();
	// Max. difference for correct validation
	private double deltaCorrect = 0.2;

	// Max. difference for acceptable Value
	private double deltaCorrectRange = 0.5;

	// Range for random Number of pieces
	private static final int MAXNUMPIECES = 30;

	// To testing Agent
	private Agent pa;

	// Referee
	private AlphaBetaAgent referee;

	// Opening-Books
	private BookSum books;

	// Progress for Multi-Tests (0-100)
	private int progress = 0;

	/**
	 * @param pa
	 *            to testing agent
	 * @param refereeBooks
	 *            opening books for the referee
	 */
	public ValueFuncTest(Agent pa, BookSum refereeBooks, AlphaBetaAgent referee) {
		// referee = new AlphaBetaAgent(refereeBooks);
		this.referee = referee;
		this.pa = pa;
		books = refereeBooks;
	}

	/**
	 * Single Test for the Value-Function
	 * 
	 * @param board
	 * @return Result of the single test
	 */
	public ResultValueFuncSingle test(int board[][]) {
		ResultValueFuncSingle vfr = new ResultValueFuncSingle();

		long zstStart = System.currentTimeMillis();

		double realVal = referee.getScore(board, true);
		long zstStop = System.currentTimeMillis();
		vfr.timeReferee = zstStop - zstStart;

		zstStart = System.currentTimeMillis();
		double agentVal = pa.getScore(board, true);
		zstStop = System.currentTimeMillis();

		vfr.agent = pa.getName();
		vfr.isCorrect = Math.abs(realVal - agentVal) < deltaCorrect;
		vfr.isCorrectRange = Math.abs(realVal - agentVal) < deltaCorrectRange;
		vfr.board = board;
		vfr.realValue = realVal;
		vfr.agentValue = agentVal;
		vfr.timeAgent = zstStop - zstStart;
		vfr.deltaCorrect = deltaCorrect;
		vfr.deltaCorrectRange = deltaCorrectRange;
		return vfr;
	}

	/**
	 * Multiple Test
	 * 
	 * @param boards
	 *            Table, containing multiple boards
	 * @return Result of the multiple test
	 */
	public ResultValueFuncMulti test(int boards[][][]) {
		ResultValueFuncMulti vfrm = new ResultValueFuncMulti();
		int numBoards = boards.length;

		vfrm.vfr = new ResultValueFuncSingle[numBoards];
		vfrm.totalNum = numBoards;

		// Times
		long totalTimeAgent = 0;
		long totalTimeReferee = 0;

		// Counters
		int correctNum = 0;
		int correctRangeNum = 0;

		for (int i = 0; i < numBoards; i++) {
			// Progress
			progress = ((i + 1) * 100) / numBoards;

			ResultValueFuncSingle v = test(boards[i]);
			vfrm.vfr[i] = v;
			totalTimeAgent += v.timeAgent;
			totalTimeReferee += v.timeReferee;
			if (v.isCorrect)
				correctNum++;
			else if (v.isCorrectRange)
				correctRangeNum++;
		}
		vfrm.correctNum = correctNum;
		vfrm.correctRangeNum = correctRangeNum;
		vfrm.wrongNum = numBoards - correctNum - correctRangeNum;
		vfrm.totalTimeAgent = totalTimeAgent;
		vfrm.totalTimeReferee = totalTimeReferee;
		return vfrm;
	}

	/**
	 * randomly generates Boards with numPieces pieces. If numPieces is
	 * negative, then the number of pieces is selected randomly
	 * 
	 * @param numBoards
	 * @param numPieces
	 * @return Result of the multiple test
	 */
	public ResultValueFuncMulti test(int numBoards, int numPieces) {
		int[][][] boards = new int[numBoards][][];

		if (numPieces > MAXNUMPIECES)
			numPieces = MAXNUMPIECES;
		for (int i = 0; i < numBoards; i++) {
			progress = ((i + 1) * 100) / numBoards;
			boards[i] = generateRandBoard(numPieces);
		}
		return test(boards);
	}

	/**
	 * @param numPieces
	 * @return a randomly generated legal board
	 */
	private int[][] generateRandBoard(int numPieces) {
		RandomAgent ra = new RandomAgent();
		ConnectFour c4 = new ConnectFour();
		int nP = (numPieces < 0 ? rand.nextInt(MAXNUMPIECES) : numPieces);
		boolean isLegalBoard;
		do {
			isLegalBoard = true;
			c4.resetBoard();
			for (int j = 0; j < nP; j++) {
				int mv = ra.getBestMove(c4.getBoard());
				if (c4.canWin(mv)) {
					isLegalBoard = false;
					break;
				}
				c4.putPiece(mv);
			}
		} while (!isLegalBoard);
		return c4.getBoard();
	}

	/**
	 * Takes all boards out of the 12-ply database. Allows fast comparison of
	 * the values by the referee
	 * 
	 * @param numBoards
	 * @param randomChoiceDB
	 * @return Result of the multiple test
	 */
	public ResultValueFuncMulti testDB(int numBoards, boolean randomChoiceDB) {
		int[][][] boards = new int[numBoards][7][6];
		int k = 0;
		openingBook.Book bk = books.getOpeningBookDeepDist();
		for (int i = 0; i < numBoards && k < bk.getBookSize(); i++) {
			progress = ((i + 1) * 100) / numBoards;
			if (randomChoiceDB)
				k = rand.nextInt(bk.getBookSize());
			bk.getBoard(k, boards[i]);
			k += (bk.getBookSize() / numBoards);
		}
		return test(boards);
	}

	/**
	 * A single test for finding the best Move by the agent.
	 * 
	 * @param board
	 * @param realBestmove
	 * @return Result
	 */
	public ResultBestMoveSingle bestMoveTest(int board[][], int realBestmove) {
		ResultBestMoveSingle bmr = new ResultBestMoveSingle();

		long zstStart = System.currentTimeMillis();
		int agentbestMove = pa.getBestMove(board);
		long zstStop = System.currentTimeMillis();

		bmr.agent = pa.getName();
		bmr.isCorrect = realBestmove == agentbestMove;
		bmr.board = board;
		bmr.realBestMove = realBestmove;
		bmr.agentBestMove = agentbestMove;
		bmr.timeAgent = zstStop - zstStart;
		return bmr;
	}

	/**
	 * Tests the "best-Move-Finding" for random boards with numPieces pieces. If
	 * numPieces is negative, its value will be set randomly.
	 * 
	 * @param numBoards
	 * @param numPieces
	 * @return Result
	 */
	public ResultBestMoveMulti bestMoveTest(int numBoards, int numPieces) {
		long zstStart = System.currentTimeMillis();
		ResultBestMoveSingle[] bmr = new ResultBestMoveSingle[numBoards];

		int correctCount = 0;
		int wrongCount = 0;
		ConnectFour c4 = new ConnectFour();

		for (int i = 0; i < numBoards; i++) {
			progress = ((i + 1) * 100) / numBoards;

			boolean isLegal;
			int board[][];
			int bestmove = -1;
			do {
				isLegal = true;

				board = generateRandBoard(numPieces);
				c4.setBoard(board);

				// Don't allow boards with direct threats, because there will
				// only be one best-Move that is too easy to find
				if (c4.hasWin(ConnectFour.PLAYER1)
						|| c4.hasWin(ConnectFour.PLAYER2)) {
					isLegal = false;
					continue;
				}
				double[] vTable = referee.getNextVTable(board, true);
				int countWins = 0;

				// Check, if there is only one best move for the board
				for (int j = 0; j < vTable.length; j++)
					if (c4.countPieces() % 2 == 0) {
						if (vTable[j] > 0) {
							countWins++;
							bestmove = j;
						}
					} else {
						if (vTable[j] < 0) {
							countWins++;
							bestmove = j;
						}
					}
				if (countWins != 1) {
					isLegal = false;
				}
			} while (!isLegal);

			// Test the agent
			bmr[i] = bestMoveTest(board, bestmove);
			if (bmr[i].isCorrect)
				correctCount++;
			else
				wrongCount++;
		}
		long zstStop = System.currentTimeMillis();

		ResultBestMoveMulti bmrm = new ResultBestMoveMulti();
		bmrm.bmr = bmr;
		bmrm.totalTime = zstStop - zstStart;
		bmrm.correctNum = correctCount;
		bmrm.wrongNum = wrongCount;
		bmrm.totalNum = numBoards;

		return bmrm;
	}

	/**
	 * Max. difference for correct validation
	 * 
	 * @param deltaCorrect
	 */
	public void setDeltaCorrect(double deltaCorrect) {
		this.deltaCorrect = deltaCorrect;
	}

	/**
	 * 
	 * Max. difference for acceptable Value
	 * 
	 * @param deltaCorrectRange
	 */
	public void setDeltaCorrectRange(double deltaCorrectRange) {
		this.deltaCorrectRange = deltaCorrectRange;
	}

	@Override
	public int getProgress() {
		return progress;
	}

	@Override
	public String getStatusMessage() {
		String str = "Testing the Value Function ...";
		return str;
	}
}

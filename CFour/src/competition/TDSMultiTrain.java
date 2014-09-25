package competition;

import java.text.DecimalFormat;

import nTupleTD.TDParams;
import nTupleTD.TDSAgent;

import openingBook.BookSum;
import guiOptions.OptionsMultiTrain;
import c4.Agent;
import c4.AlphaBetaAgent;

//TODO: Möglichkeit zum abbrechen
//TODO: Nur bis zu einer gewissen Tiefe die Competitions durchführen (erlernen der Eröffnung prüfen)
/**
 * Allows several training-procedures for one TD-Agent. After X games there is
 * the possibility to measure the strength of the Agent. This can be done by
 * matches against a Alpha-Beta- or a random-Agent. It is also possible to use
 * different initial boards for the single matches. The number of
 * wins/draws/defeats are saved in a table and can be examined. (class
 * ResultMultiTrain). The Progress of the training can be fetched, if this
 * should be needed.
 * 
 * @author Markus Thill
 * 
 */
public class TDSMultiTrain implements Progress {

	// Single Match against the Opponent
	public static final int SIMPLEMATCH = 1;

	// Simple mathes against the opponent, but use more initial boards: current
	// board + other ones
	public static final int MULTIMATCH = 2;
	private static final int INITBOARDS[][][] = { //
	// Board 0
			{ { 0, 0, 0, 0, 0, 0 },//
					{ 0, 0, 0, 0, 0, 0 }, //
					{ 2, 0, 0, 0, 0, 0 }, //
					{ 1, 0, 0, 0, 0, 0 },//
					{ 0, 0, 0, 0, 0, 0 }, //
					{ 0, 0, 0, 0, 0, 0 }, //
					{ 0, 0, 0, 0, 0, 0 } }, //

			// Board 1
			{ { 0, 0, 0, 0, 0, 0 },//
					{ 2, 0, 0, 0, 0, 0 }, //
					{ 0, 0, 0, 0, 0, 0 }, //
					{ 1, 0, 0, 0, 0, 0 },//
					{ 0, 0, 0, 0, 0, 0 }, //
					{ 0, 0, 0, 0, 0, 0 }, //
					{ 0, 0, 0, 0, 0, 0 } }, //

			// Board 2
			{ { 4, 0, 0, 0, 0, 0 },//
					{ 0, 0, 0, 0, 0, 0 }, //
					{ 0, 0, 0, 0, 0, 0 }, //
					{ 1, 0, 0, 0, 0, 0 },//
					{ 0, 0, 0, 0, 0, 0 }, //
					{ 0, 0, 0, 0, 0, 0 }, //
					{ 0, 0, 0, 0, 0, 0 } } //

	// ...
	};//

	private static int measureMethods[] = { SIMPLEMATCH, MULTIMATCH };

	// Agent to be trained
	TDSAgent tds = null;

	// AlphaBeta- or Random-Agent for the matches
	Agent opponent = null;

	// Referee for the competitions. If the opponent is an Alpha-Beta-Agent,
	// then the referee will be set to this AB-Agent
	AlphaBetaAgent referee = null;

	// Params for single Agent-Training
	// TDParams tdPar;

	// Number of Training-procedures
	int trainNum = 0;
	// int curTrainNum;

	// Method, for measuring the Strength of the TD-Agent
	int measureStrengthMethod = 0;

	// Interval for the Strength-Measurements
	int measureInterval = 0;

	// Number of Intervals
	// int numMeasureInterval;

	// Number of Strength-Measurements after each interval
	int numMeasures = 0;

	// Initial Board
	int initBoard[][] = null;

	// Boards for Multi-match-Mode
	int multiBoards[][][] = null;

	// Allows matches against AB-Agent
	Competition competition = null;

	// Competition-Options
	boolean swapPlayers = false;
	boolean useCurBoard = false;
	boolean logValues = false;

	// Results for simple matches against the opponent
	// results[trainNr][intervalNr]
	ResultCompMulti[][] results = null;

	// countScores[interval{0,1,2...}][remis 0 / win 1 / loss 2]
	int countScores[][] = null;

	// Progress of the multi-training
	int progress;

	/**
	 * @param tds
	 *            The td-Agent that shall be trained
	 * @param opponent
	 *            The opponent for the matches
	 * @param books
	 *            opening-books needed for the competitions
	 * @param ops
	 *            The selected options
	 * @param initBoard
	 *            initial board for the matches
	 */
	public TDSMultiTrain(TDSAgent tds, Agent opponent, AlphaBetaAgent referee,
			BookSum books, OptionsMultiTrain ops, int initBoard[][]) {

		// Set TD-Agent
		this.tds = tds;

		// Set Options
		this.measureInterval = ops.getMeasureInterval();
		this.trainNum = ops.getNumTrainingGames();
		this.numMeasures = ops.getNumMatches();
		this.measureStrengthMethod = measureMethods[ops.getMeasureMethod()];
		this.swapPlayers = ops.swapPlayers();
		this.useCurBoard = ops.useCurBoard();
		this.logValues = ops.logValues();

		// Randomize Losses for opening-phase, if opponent is an AB-Agent
		AlphaBetaAgent ab = null;
		if (opponent instanceof AlphaBetaAgent) {
			ab = (AlphaBetaAgent) opponent;
			ab.randomizeLosses(true);
		}

		// Set opponent and referee
		this.referee = null;
		this.opponent = opponent;
		// if (opponent instanceof AlphaBetaAgent)
		// this.referee = (AlphaBetaAgent) opponent;
		if (logValues) // Only init, if values shall be logged
			this.referee = referee;

		// Set initial-Board
		this.initBoard = initBoard;

		// Init Competition
		competition = new Competition(tds, opponent, referee);

		// Init Results
		TDParams newTDPar = tds.getTDParams();
		int interValNum = newTDPar.trainGameNum / measureInterval;
		results = new ResultCompMulti[trainNum][interValNum];

		if (measureStrengthMethod == MULTIMATCH) {
			// Copy current board to multiMtach-Array

			// +1 for current board
			int numBoards = INITBOARDS.length + 1;
			multiBoards = new int[numBoards][][];
			multiBoards[0] = initBoard;
			for (int i = 1; i < numBoards; i++)
				multiBoards[i] = INITBOARDS[i - 1];
		}

		// Reinit TDS-Agent (could already be trained)
		newTDPar.randNTuples = false;
		tds.reinitTDSAgent(newTDPar);

		//
		if (ab != null)
			ab.randomizeLosses(false);

		// Try to delete old N-Tuple-LUTs
		System.gc();
	}

	/**
	 * @param printCurAction
	 *            true, if the current actions made shall be printed to the
	 *            console
	 * @param printAlphaEpsilon
	 *            true, if alpha and epsilon shall be printed to console
	 * @return The relevant information for the executed training
	 */
	public ResultTrainMulti startMultiTrain(boolean printCurAction,
			boolean printAlphaEpsilon) {

		// execute the training
		multiTrain(printCurAction, printAlphaEpsilon);
		ResultTrainMulti rtm = new ResultTrainMulti();
		rtm.avgMovesMade = updateCountScores();

		// Set all results needed
		rtm.results = results;
		rtm.opponent = opponent.getName();
		rtm.trainNum = trainNum;
		rtm.gameNum = tds.getTDParams().trainGameNum;
		rtm.measureStrengthMethod = measureStrengthMethod;
		rtm.measureInterval = measureInterval;
		rtm.numMeasures = numMeasures;
		rtm.initBoard = initBoard;
		rtm.multiBoards = multiBoards;
		rtm.countScores = countScores;
		rtm.numIntervals = results[0].length;
		rtm.nTuples = tds.m_Net.toString(1);
		rtm.tdPar = tds.getTDParams();
		return rtm;
	}

	/**
	 * Multi-training
	 * 
	 * @param printCurAction
	 *            true, if the current actions made shall be printed to the
	 *            console
	 * @param printAlphaEpsilon
	 *            true, if alpha and epsilon shall be printed to console
	 */
	private void multiTrain(boolean printCurAction, boolean printAlphaEpsilon) {
		TDParams tdPar = tds.getTDParams();
		int gameNum = tdPar.trainGameNum;
		int intervalNr;
		int player = 1;
		DecimalFormat df = new DecimalFormat("+0.00000;-0.00000");
		if (printCurAction)
			System.out.println("[Start Multi-Training]");
		for (int i = 0; i < trainNum; i++) {
			for (int j = 0; j < gameNum; j++) {
				tds.trainNet(player);
				if (j % measureInterval == measureInterval - 1) {
					// Print Action
					if (printCurAction) {
						System.out.println("\nTraining-Num: " + (i + 1)
								+ " ; Game: " + (j + 1));
						System.out.println("Start Competition ... ");
					}
					intervalNr = j / measureInterval;
					competition.setInitialBoard(initBoard);

					// Check the method of measuring the strength
					switch (measureStrengthMethod) {
					case SIMPLEMATCH:
						// Check if Agent i not completely trained
						if (tdPar.stopAfterMoves < 40)
							results[i][intervalNr] = competition.multiCompete(
									numMeasures, tdPar.stopAfterMoves,
									swapPlayers, logValues);
						else
							results[i][intervalNr] = competition.multiCompete(
									numMeasures, swapPlayers, logValues);
						break;
					case MULTIMATCH:
						// Check if Agent i not completely trained
						if (tdPar.stopAfterMoves < 40)
							results[i][intervalNr] = competition.multiCompete(
									tdPar.stopAfterMoves, multiBoards,
									swapPlayers, logValues);
						else
							results[i][intervalNr] = competition.multiCompete(
									multiBoards, swapPlayers, logValues);
						break;
					}

					// Print current Action, if wanted
					if (printCurAction) {
						double score = results[i][intervalNr].numWonX
								- results[i][intervalNr].numWonO;
						score /= results[i][intervalNr].numGames;
						System.out.println("Competition completed. Score: "
								+ df.format(score));
					}

					// Print Alpha and Epsilon, if wanted
					if (printAlphaEpsilon)
						System.out.println("alpha:" + df.format(tds.getAlpha())
								+ " ; epsilon: " + df.format(tds.getEpsilon()));

				}
				progress = ((i * gameNum + j) * 100) / (trainNum * gameNum);
			}
			if (i < trainNum - 1)
				tds.reinitTDSAgent(tds.getTDParams());
		}
	}

	/**
	 * Calculate the number of wins/draws/defeats for the single game-intervals
	 * 
	 * @return Average Number of moves made in all matches against the opponent
	 */
	private float updateCountScores() {
		// results[trainNr][intervalNr]
		int intervalNum = results[0].length;
		countScores = new int[intervalNum][4];
		float avgMoves = (float) 0.0;
		for (int i = 0; i < intervalNum; i++) {
			for (int j = 0; j < results.length; j++) {
				countScores[i][0] += results[j][i].numDraw;// remis
				countScores[i][1] += results[j][i].numWonX; // win
				countScores[i][2] += results[j][i].numWonO; // loss
				countScores[i][3] += (results[j][i].numDraw
						+ results[j][i].numWonX + results[j][i].numWonO);
				avgMoves += results[j][i].avgMovesNum;
			}
		}
		return avgMoves / (intervalNum * results.length);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Progress#getStatusMessage()
	 */
	@Override
	public String getStatusMessage() {
		return "Started Multi-Training ..." + progress + "%";
	}
}

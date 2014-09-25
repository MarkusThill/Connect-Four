package competition;

import java.text.DecimalFormat;

import nTupleTD.TDParams;

import c4.ConnectFour;

/**
 * Results for a multi-Training (class TDSMultiTrain)
 * 
 * @author Markus Thill
 * 
 */
public class ResultTrainMulti implements Result {

	// results[trainNr][intervalNr]
	ResultCompMulti[][] results = null;

	// Opponent
	String opponent;

	// Number of Training-procedures
	int trainNum = 0;

	// Number of Games for one training
	int gameNum;

	// Method, for measuring the Strength of the TD-Agent
	int measureStrengthMethod = 0;

	// Interval for the Strength-Measurements
	int measureInterval = 0;

	// Number of Intervals
	int numIntervals = 0;

	// Number of Intervals
	// int numMeasureInterval;

	// Number of Strength-Measurements after each interval
	int numMeasures = 0;

	// Initial Board
	int initBoard[][] = null;

	// Boards for Multi-match-Mode
	int multiBoards[][][] = null;

	// countScores[interval{0,1,2...}][remis 0 / win 1 / loss 2]
	int countScores[][] = null;

	// Average Number of Moves made in each competition
	float avgMovesMade = 0;

	// N-Tuples
	String nTuples = null;

	// TDParams
	TDParams tdPar = null;

	/**
	 * @param title
	 * @return Result as formatted String
	 */
	public String getResult(String title) {
		String str = new String();
		str += ("==============================================\n");
		str += (title + "\n");
		str += ("==============================================\n");
		str += ("Opponent: " + opponent);
		str += ("\nNumber of Traing procedures: " + trainNum + "\n");
		str += ("Number of Games for one training: " + gameNum + "\n");
		str += ("Interval for the strength-measurements: " + measureInterval + "\n");
		str += ("Number of strength-measurements after each interval: "
				+ numMeasures + "\n");
		str += ("Average Number of moves made in all competitions: "
				+ avgMovesMade + "\n");

		switch (measureStrengthMethod) {
		case TDSMultiTrain.SIMPLEMATCH:
			str += ("Method to meausure strength: Single Match against the Opponent\n");
			str += ("Board: \n\n");
			str += (ConnectFour.toString(initBoard));
			break;
		case TDSMultiTrain.MULTIMATCH:
			str += ("Method to measure strength: Simple matches against the opponent, \n"
					+ "but use more initial boards: current board + specified in class TDSMultiTrain\n");
			str += ("Board: \n\n");
			for (int i = 0; i < multiBoards.length; i++) {
				str += (ConnectFour.toString(multiBoards[i]));
				str += "\n";
			}
			break;
		}

		str += ("==============================================\n");
		str += ("Results:\n");
		str += ("==============================================\n\n");
		/*
		 * for (int i = 0; i < countScores.length; i++) { str += ("Games: " + (i
		 * + 1) * measureInterval + "\n"); str +=
		 * ("Total Count of Competitions: " + countScores[i][3] + "\n"); str +=
		 * ("Number of draws against opponent: " + countScores[i][0] + "\n");
		 * str += ("Number of wins against opponent: " + countScores[i][1] +
		 * "\n"); str += ("Number of losses against opponent: " +
		 * countScores[i][2] + "\n"); str +=
		 * ("-------------------------------------------------\n"); }
		 */
		DecimalFormat df = new DecimalFormat("+0.00000;-0.00000");
		String table[][] = new String[countScores.length + 2][6];
		table[0][0] = "Game";
		table[0][1] = "Count";
		table[0][2] = "Draw";
		table[0][3] = "Win";
		table[0][4] = "Defeat";
		table[0][5] = "Score";
		table[1][0] = "-----------";
		table[1][1] = "-----------";
		table[1][2] = "-----------";
		table[1][3] = "-----------";
		table[1][4] = "-----------";
		table[1][5] = "-----------";
		for (int i = 0; i < countScores.length; i++) {
			table[i + 2][0] = ((i + 1) * measureInterval + "");
			table[i + 2][1] = (countScores[i][3] + "");
			table[i + 2][2] = (countScores[i][0] + "");
			table[i + 2][3] = (countScores[i][1] + "");
			table[i + 2][4] = (countScores[i][2] + "");

			// calc score
			double score = (double) (countScores[i][1] - countScores[i][2])
					/ (double) countScores[i][3];
			table[i + 2][5] = df.format(score);
		}
		str += formatTable(table);

		str += ("\n\nTD-Params\n");
		str += ("==============================================\n");
		str += ("Number Games: " + tdPar.trainGameNum + "\n");
		str += ("Stop after Moves: " + tdPar.stopAfterMoves + "\n");
		str += ("Possible Values/Field: " + tdPar.posVals + "\n");
		str += ("Alpha: " + tdPar.alphaInit + " -> " + tdPar.alphaFinal + "\n");
		str += ("Epsilon: " + tdPar.epsilonInit + " -> " + tdPar.epsilonFinal + "\n");
		str += ("Gamma: " + tdPar.gamma + "\n");
		str += ("Lambda: " + tdPar.lambda + "\n");
		str += ("Number of N-Tuples: " + tdPar.tupleNum + "\n");
		str += ("Radnom Init of all weights: " + tdPar.randInitWeights + "\n");
		str += ("\nUsed N-Tuples:\n" + nTuples + "\n");
		str += ("==============================================\n");
		return str;
	}

	/**
	 * Print the result to the console
	 * 
	 * @param title
	 */
	public void printResult(String title) {
		System.out.print(getResult(title));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Result#getNum()
	 */
	@Override
	public int getNum() {
		return numIntervals * trainNum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Result#getOverViewResult()
	 */
	@Override
	public String getOverViewResult() {
		return getResult("Overview");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Result#getSingleResult(int, boolean)
	 */
	@Override
	public String getSingleResult(int index, boolean allBoards) {
		int trainNr = index / numIntervals;
		int interval = index % numIntervals;
		ResultCompMulti result = results[trainNr][interval];
		String str = new String();
		str += ("Single Result\n\n");
		str += ("Training-Number: " + (trainNr + 1) + "\n");
		str += ("Games: " + (interval + 1) * measureInterval + "\n");
		return result.getResult(str);
	}

	/**
	 * Formats a Table of Strings, so that they can be displayed properly
	 * 
	 * @param table
	 *            All Elements of the Table [Rows][Columns]
	 * @return The formated table, ready to print to the console
	 */
	public static String formatTable(String[][] table) {
		// Find out what the maximum number of columns is in any row
		int maxColumns = 0;
		for (int i = 0; i < table.length; i++) {
			maxColumns = Math.max(table[i].length, maxColumns);
		}

		// Find the maximum length of a string in each column
		int[] lengths = new int[maxColumns];
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				lengths[j] = Math.max(table[i][j].length(), lengths[j]);
			}
		}

		// Generate a format string for each column
		String[] formats = new String[lengths.length];
		for (int i = 0; i < lengths.length; i++) {
			formats[i] = "%1$" + lengths[i] + "s"
					+ (i + 1 == lengths.length ? "\n" : " ");
		}

		// Put in String
		String str = new String();
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				str += String.format(formats[j], table[i][j]);
			}
		}
		return str;
	}

}

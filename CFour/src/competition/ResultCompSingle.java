package competition;

import java.text.DecimalFormat;

import c4.ConnectFour;

/**
 * used for single competitions in the class Competition
 * 
 * @author Markus Thill
 */
public class ResultCompSingle implements Result {
	public String agents[] = new String[2];
	public int startingPlayer;
	public int moves[];
	public int[][][] boards = new int[42][][]; // Starting Board with index 0
	public int expectedWinner;
	public int winner;
	public int numMovesMade;
	public long time;

	double[][][] vTables = null;

	/**
	 * @param title
	 * @param allBoards
	 * @return Single result as formatted String
	 */
	public String getResult(String title, boolean allBoards) {
		String str = new String();
		str = str.concat("==============================================\n");
		str = str.concat(title);
		str = str.concat("\n==============================================\n");
		str = str.concat("\n");
		str = str.concat("Time for Competition (in s): " + time / 1000.0
				+ "!!\n");
		str = str.concat("Competion between: \n");
		str = str.concat("\t" + agents[0] + " and\n");
		str = str.concat("\t" + agents[1] + "!!!\n");
		str = str.concat("Number of moves made: " + numMovesMade + "\n");
		str = str.concat("\n");
		if (winner != -1)
			str = str.concat("Winner: " + agents[winner] + "!!\n");
		else
			str = str.concat("No Winner: Draw!!\n");

		if (expectedWinner != -1)
			str = str.concat("Expected: " + agents[expectedWinner] + "!!\n");
		else
			str = str.concat("Expected: Draw!!\n");

		str = str.concat("\n");
		str = str.concat("Initial-Position:\n");
		str = str.concat("\n");
		str = str.concat(ConnectFour.toString(boards[0]) + "\n");

		int turn = startingPlayer;
		for (int i = 0; i < numMovesMade; i++) {
			str = str.concat("------------------------------");
			str = str.concat("------------------------------");
			str = str.concat("------------------------------\n");
			str = str.concat(i + ": Player to move: " + agents[turn] + "\n");
			if (vTables != null) {
				str = str.concat("Column\t");
				for (int j = 0; j < 7; j++)
					str = str.concat(j + "\t");
				str = str.concat("\n");

				str = str.concat("RealV:\t");
				for (int j = 0; j < 7; j++)
					str = str.concat(vTables[i][0][j] + "\t");
				str = str.concat("\n");

				str = str.concat("AgentV:\t");
				for (int j = 0; j < 7; j++)
					str = str.concat(new DecimalFormat("0.00")
							.format(vTables[i][1][j]) + "\t");
				str = str.concat("\n");
			}

			str = str.concat("Selected Move:" + moves[i] + "\n");
			str = str.concat("\n");

			if (allBoards && i < numMovesMade - 1) {
				str = str.concat("Board after Move: \n");
				str = str.concat(ConnectFour.toString(boards[i + 1]) + "\n");
			}
			str = str.concat("------------------------------");
			str = str.concat("------------------------------");
			str = str.concat("------------------------------\n");
			str = str.concat("\n");
			turn = 1 - turn;
		}
		str = str.concat("==============================================\n");
		return str;
	}

	/**
	 * @param title
	 * @param allBoards
	 */
	public void printResult(String title, boolean allBoards) {
		System.out.print(getResult(title, allBoards));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Result#getNum()
	 */
	@Override
	public int getNum() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Result#getOverViewResult()
	 */
	@Override
	public String getOverViewResult() {
		return getResult("Single-Competition", true);
	}

	// @Override
	// public String getName() {
	// return new String("Single-Competion-Result");
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Result#getSingleResult(int, boolean)
	 */
	@Override
	public String getSingleResult(int index, boolean allBoards) {
		return getResult("Single-Competition", true);
	}
}

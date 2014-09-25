package competition;

import c4.ConnectFour;

/**
 * used for multiple competitions in class Competition
 * 
 * @author Markus Thill
 * 
 */
public class ResultCompMulti implements Result {
	public ResultCompSingle[] singleResults;

	public int numGames;

	// Actual Results for all Games
	public int numWonX;
	public int numWonO;
	public int numDraw;

	// Expected Results for all Games
	public int numExpectedWonX;
	public int numExpectedWonO;
	public int numExpectedDraw;

	// Total time for all Competitions
	public long totalTime;

	// Average Number of Moves made in each game
	public float avgMovesNum;

	/**
	 * @param title
	 * @return Overview as formatted String
	 */
	public String getResult(String title) {
		String str = new String();
		str += ("==============================================\n");
		str += (title + "\n");
		str += ("==============================================\n");
		str += ("Number of Competitions: " + numGames + "\n");
		str += ("\n");
		str += ("Total Time for Competition (in s): " + totalTime / 1000.0 + "!!\n");
		str += ("Competion between: \n");
		str += ("\t" + singleResults[0].agents[0] + " and\n");
		str += ("\t" + singleResults[0].agents[1] + "!!!\n");
		str += ("Average Number of moves made per game: " + avgMovesNum + "\n");
		str += ("\n");

		str += ("Results: Actual and Expected (for Player "
				+ singleResults[0].agents[0] + ")\n");
		str += ("-------------------------------------------\n");
		str += ("\t\tWin\tLoss\tDraw\n");
		str += ("Actual:\t\t" + numWonX + "\t" + numWonO + "\t" + numDraw + "\n");
		str += ("Expected:\t" + numExpectedWonX + "\t" + numExpectedWonO + "\t"
				+ numExpectedDraw + "\n");

		str += ("\n");
		str += ("Initial-Position:\n");
		str += ("\n");
		str += ConnectFour.toString(singleResults[0].boards[0]) + "\n";
		str += ("==============================================\n");
		return str;
	}

	/**
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
		return numGames;
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
		return singleResults[index].getResult("Single Result: " + index,
				allBoards);
	}

	// @Override
	// public String getName() {
	// return new String("Multi-Competition-Result");
	// }
}

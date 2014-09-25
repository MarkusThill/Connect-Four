package competition;

/**
 * 
 * Used for multiple "Best-Move-Test" in class ValueFuncTest
 * 
 * @author Markus Thill
 */
public class ResultBestMoveMulti implements Result {

	// All Single Tests
	public ResultBestMoveSingle[] bmr;

	// Time for Evaluating in ms
	public long totalTime;

	// Counters
	public int correctNum;
	public int wrongNum;
	public int totalNum;

	/**
	 * @param title
	 * @return formatted string with results
	 */
	public String getResult(String title) {
		String str = new String();
		str += ("==============================================\n");
		str += (title + "\n");
		str += ("==============================================\n");
		str += ("\n");
		str += ("AgentType: " + bmr[0].agent + "\n");
		str += ("Total Time: (in s): " + totalTime / 1000.0 + "!!\n");

		str += ("\n");
		str += ("Total Number of Boards: " + totalNum + "\n");
		str += ("Num Correct: " + correctNum + "\n");
		str += ("Num Wrong: " + wrongNum + "\n");
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
		return totalNum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Result#getOverViewResult()
	 */
	@Override
	public String getOverViewResult() {
		return getResult("Best-Move-Test Overview");
	}

	// @Override
	// public String getName() {
	// return new String("Best Move Result");
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Result#getSingleResult(int, boolean)
	 */
	@Override
	public String getSingleResult(int index, boolean allBoards) {
		return bmr[index].getResult("Single-Result: " + index);
	}
}

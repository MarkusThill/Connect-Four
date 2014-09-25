package competition;

/**
 * used for multiple "Value-Function-Tests" in class ValueFuncTest
 * 
 * @author Markus Thill
 */
public class ResultValueFuncMulti implements Result {

	// All Single Tests
	public ResultValueFuncSingle[] vfr;

	// Time for Evaluating in ms
	public long totalTimeAgent;
	public long totalTimeReferee;

	// Counters
	public int correctNum;
	public int correctRangeNum;
	public int wrongNum;
	public int totalNum;

	/**
	 * @param title
	 * @return Overview as formatted String
	 */
	public String getResult(String title) {
		String str = new String();
		str += ("==============================================\n");
		str += (title + "\n");
		str += ("==============================================\n");
		str += ("\n");
		str += ("AgentType: " + vfr[0].agent + "\n");
		str += ("Total Time Agent (in s): " + totalTimeAgent / 1000.0 + "!!\n");
		str += ("Total Time Referee (in s): " + totalTimeReferee / 1000.0 + "!!\n");

		str += ("\n");
		str += ("Total Number of Boards: " + totalNum + "\n");
		str += ("Num Correct Values: " + correctNum + "\n");
		str += ("Num Correct-Range Values: " + correctRangeNum + "\n");
		str += ("Num Wrong-Values: " + wrongNum + "\n");
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
		return getResult("Value-Function Test Overview");
	}

	// @Override
	// public String getName() {
	// return new String("Value Function Result");
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Result#getSingleResult(int, boolean)
	 */
	@Override
	public String getSingleResult(int index, boolean allBoards) {
		return vfr[index].getResult("Single Result: " + index);
	}
}

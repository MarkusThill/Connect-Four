package competition;

import c4.ConnectFour;

/**
 * used for single "Value-Function-Tests" in class ValueFuncTest
 * 
 * @author Markus Thill
 */
public class ResultValueFuncSingle implements Result {

	String agent;
	public boolean isCorrect;
	public boolean isCorrectRange;

	public int board[][];
	public double realValue;
	public double agentValue;

	// Time for Evaluating in ms
	public long timeAgent;
	public long timeReferee;

	// Max. difference for correct validation
	public double deltaCorrect = 0.2;

	// Max. difference for acceptable Value
	public double deltaCorrectRange = 0.5;

	/**
	 * @param title
	 * @return single result as formatted String
	 */
	public String getResult(String title) {
		String str = new String();
		str += ("==============================================\n");
		str += (title + "\n");
		str += ("==============================================\n");
		str += ("\n");
		str += ("AgentType: " + agent + "\n");
		str += ("Time Agent (in s): " + timeAgent / 1000.0 + "!!\n");
		str += ("Time Referee (in s): " + timeReferee / 1000.0 + "!!\n");
		str += ("\n");
		str += ("Board: \n");
		str += ConnectFour.toString(board);
		str += ("\n");
		str += ("Agent value: " + agentValue + "\n");
		str += ("Real value: " + realValue + "\n");
		if (isCorrect)
			str += ("Agent Value was correct!!!\n");
		if (isCorrectRange)
			str += ("Agent Value was in correct Range!!!\n");
		else
			str += ("Agent Value was NOT correct!!!\n");
		str += ("Max. Spread for correct Value and correct Range: dC="
				+ deltaCorrect + " , dCR=" + deltaCorrectRange + "\n");
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
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Result#getOverViewResult()
	 */
	@Override
	public String getOverViewResult() {
		return getResult("Single Value-Function-Test");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Result#getSingleResult(int, boolean)
	 */
	@Override
	public String getSingleResult(int index, boolean allBoards) {
		return getResult("Single Value-Function-Test");
	}
}

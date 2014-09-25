package competition;

import c4.ConnectFour;

/**
 * 
 * Used for "Best-Move-Test" in class ValueFuncTest
 * 
 * @author Markus Thill
 * 
 */
public class ResultBestMoveSingle implements Result {

	String agent;
	public boolean isCorrect;

	public int board[][];
	public int realBestMove;
	public int agentBestMove;

	// Time for Evaluating in ms
	public long timeAgent;

	/**
	 * @param title
	 * @return result as formatted string
	 */
	public String getResult(String title) {
		String str = new String();
		str += ("==============================================\n");
		str += (title + "\n");
		str += ("==============================================\n");
		str += ("\n");
		str += ("AgentType: " + agent + "\n");
		str += ("Time Agent (in s): " + timeAgent / 1000.0 + "!!\n");

		str += ("\n");
		str += ("Board: \n");
		str += ConnectFour.toString(board);
		str += ("\n");
		str += ("Agent best move: " + agentBestMove + "\n");
		str += ("Real best move: " + realBestMove + "\n");
		if (isCorrect)
			str += ("Agent Value was correct!!!\n");
		else
			str += ("Agent Value was NOT correct!!!\n");
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
		return getResult("Best-Move Single-Test");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see competition.Result#getSingleResult(int, boolean)
	 */
	@Override
	public String getSingleResult(int index, boolean allBoards) {
		return getResult("Best-Move Single-Test");
	}
}

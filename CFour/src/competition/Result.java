package competition;

/**
 * 
 * Interface for all result-classes, to get the most important information.
 * 
 * @author Markus Thill
 */
public interface Result {

	/**
	 * @return Number of games played or boards evaluated
	 */
	public int getNum();

	/**
	 * @return Overview of the results
	 */
	public String getOverViewResult();

	/**
	 * @param index
	 *            Number of the single-result
	 * @param allBoards
	 *            set true, if the board shall be printed to the screen for each
	 *            move
	 * @return the selected result as formatted String
	 */
	String getSingleResult(int index, boolean allBoards);
}

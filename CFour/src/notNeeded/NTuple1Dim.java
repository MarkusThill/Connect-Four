package nTuple;


public class NTuple1Dim extends NTupleBase
{
	private int[] nTuple;
	
	public NTuple1Dim(int length, int posVals, int[] nTuple) {
		super(length, posVals);
		this.nTuple = nTuple;
	}
	
	public NTuple1Dim(int length, int posVals, int boardSize) {
		super(length, posVals);
		
		boolean unique;
		int rand, i, j;
		for(i=0;i<length;i++) {
			unique = false;
			do {
				rand = rnd.nextInt(boardSize);
				for(j=0;j<i;j++)
					if(nTuple[j] == rand)
						continue;
				unique = true;
			} while(!unique);
		}
	}
	
	/**
	 * @param board	negative values are not allowed! 
	 * @return	index for the LUT of this board
	 */
	public int getLUTIndex(int[] board) {
		int index = 0;
		int pow = 1;
		for(int i=0;i<nTuple.length;i++) {
			index += pow * board[nTuple[i]];
			pow *= posVals;
		}
		return index;
	}
	
	public double getScore(int[] board) {
		return lut[getLUTIndex(board)];		
	}
	
	public void update(int[] board, double dW) {
		lut[getLUTIndex(board)] += dW;
	}
}

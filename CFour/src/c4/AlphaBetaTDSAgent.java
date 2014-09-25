package c4;

import nTupleTD.TDParams;
import nTupleTD.TDSAgent;
import openingBook.BookSum;

public class AlphaBetaTDSAgent extends AlphaBetaAgent{


	private static final long serialVersionUID = 4848218715550054109L;
	private TDSAgent td;
	
	
	public AlphaBetaTDSAgent(BookSum books, TDSAgent tds) {
		super(books);
		td = tds;
		//setDifficulty(td.getTDParams().nPlyLookAhead);
	}
	
	@Override
	public int evaluate(int curPlayer, long zobr) {
		if(!td.getTDParams().useHashExtern)
			zobr = -1L;
		return (int)(td.m_Net.getValue(curPlayer, zobr, new long[]{fieldP1, fieldP2}) * 500);
	}
	
	@Override
	public String getName() {
		return new String("Minimax-TDS");
	}
	
	public TDParams getTDParams() {
		return td.getTDParams();
	}
}

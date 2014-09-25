package nTuple;

import c4.TDParams;

public class BiasWeight {
	private double bias = 0.0;
	private double biasA = 0.1;
	private double biasN = 0.1;
	private double biasAccu = 0.0;
	
	private TDParams tdPar;
	
	BiasWeight(TDParams tdPar) {
		this.tdPar = tdPar;
		bias = 0.0;
	}
	
	public void update(int curPlayer, long[] board, long zobr, double dW,
			double delta, double grad, boolean accumulate) {
		switch (updateMeth) {
		case TDL:
			bias += dW;
			break;
		case TCL:
			// Temporal Coherence
			// double tcFactor = Math.abs(biasN) / biasA;
			// // update tables
			// double r = delta * grad; // Recommended change
			// biasN += r;
			// biasA += Math.abs(r);
			// bias += dW * tcFactor;
			break;
		case IDBD:
			// TODO: Implement for IDBD
			break;
		default:
			break;
		}
	}
}

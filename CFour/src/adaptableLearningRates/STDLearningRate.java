package adaptableLearningRates;

import nTupleTD.TDParams;
import nTupleTD.UpdateParams;

public final class STDLearningRate extends LearningRates {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5728153590856247362L;

	public STDLearningRate(TDParams tdPar) {
		super(null, tdPar); // no - for all weights common - elements
	}

	@Override
	public void preWeightUpdateTask(UpdateParams u_i) {
	}

	@Override
	public double getLearningRate(UpdateParams u_i) {
		return u_i.globalAlpha;
	}

	@Override
	public void postWeightUpdateTask(UpdateParams u_i) {
	}

	@Override
	public TDParams getBestParams(TDParams tdPar) {
		TDParams bestTDPar = null;
		try {
			bestTDPar = (TDParams) tdPar.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		bestTDPar.alphaInit = 0.004;
		bestTDPar.alphaFinal = 0.002;

		// Elig-Traces: we choose the variant [rr]. Resetting and replacing
		// traces with lambda = 0.8
		bestTDPar.lambda = 0.8;
		bestTDPar.replacingTraces = true;
		bestTDPar.resetEligOnRandomMove = true;

		return bestTDPar;
	}

}

package adaptableLearningRates;

import nTupleTD.TDParams;
import nTupleTD.UpdateParams;
import nTupleTD.ValueFuncC4;

/**
 * Implementation of Dabney and Barto's Alpha-Bounds algorithm. The main part of
 * this algorithm is implemented in {@link ValueFuncC4}.
 * 
 * @author Markus Thill
 * 
 */
public class AlphaBound extends LearningRates {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7351329370067886596L;

	/**
	 * Alpha-Bounds has no in-memory parameters, therefore the constructor only
	 * calls the parent-constructor.
	 */
	public AlphaBound(TDParams tdPar) {
		super(null, tdPar);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adaptableLearningRates.LearningRates#preWeightUpdateTask(nTupleTD.
	 * UpdateParams)
	 */
	@Override
	public void preWeightUpdateTask(UpdateParams u_i) {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * adaptableLearningRates.LearningRates#getLearningRate(nTupleTD.UpdateParams
	 * )
	 */
	@Override
	public double getLearningRate(UpdateParams u_i) {
		return u_i.globalAlpha;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adaptableLearningRates.LearningRates#postWeightUpdateTask(nTupleTD.
	 * UpdateParams)
	 */
	@Override
	public void postWeightUpdateTask(UpdateParams u_i) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * adaptableLearningRates.LearningRates#getBestParams(nTupleTD.TDParams)
	 */
	@Override
	public TDParams getBestParams(final TDParams tdPar) {
		TDParams bestTDPar = null;
		try {
			bestTDPar = (TDParams) tdPar.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		// Set alpha-Init to 1.0
		bestTDPar.alphaInit = 1.0;

		// Elig-Traces: we choose the variant [rr]. Resetting and replacing
		// traces with lambda = 0.8
		bestTDPar.lambda = 0.8;
		bestTDPar.replacingTraces = true;
		bestTDPar.resetEligOnRandomMove = true;

		return bestTDPar;
	}

}

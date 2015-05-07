package adaptableLearningRates;

import nTupleTD.TDParams;
import nTupleTD.UpdateParams;

public class ALAP extends LearningRates {
	private static final long serialVersionUID = 1790776087103691783L;
	public static final double LOWERBOUND_ALPHA = 1e-6;

	// TODO: As real Parameters
	private static final double gamma = 0.02; 
	private static final double theta = 0.02;

	/**
	 * The gradient of the loss function for the weights w_ij from the previous
	 * iteration
	 */
	private float[] lastGradient;

	/**
	 * Vector-elements for the element-wise normalization of the meta-descent
	 * update
	 */
	private float[] v;

	private float[] alpha;

	public ALAP(TDParams tdPar, int length) {
		super(null, tdPar);
		createTables(length);
	}

	private void createTables(int length) {
		lastGradient = new float[length];
		v = new float[length];
		alpha = new float[length];
		for (int i = 0; i < length; i++) {
			alpha[i] = (float) tdPar.alphaInit;
		}
	}

	@Override
	public void preWeightUpdateTask(UpdateParams u_i) {
		int i = u_i.i;

		//
		// Calculate new v_i
		//
		double grad = -u_i.e_i * u_i.delta; //TODO: Minus here, or it doesn't matter???????
		double vNew = (1 - gamma) * v[i] + gamma * (grad * grad);
		v[i] = (float) vNew;

		//
		// Calculate new alpha_i
		//
		if (v[i] != 0.0) {
			double a = alpha[i] * (1.0 + theta * grad * lastGradient[i] / v[i]);
			alpha[i] = (float) a;
		}

		//
		// Check, if alpha_i is smaller than the lower-bound. Set new value in
		// case.
		//
		if (alpha[i] < LOWERBOUND_ALPHA) {
			alpha[i] = (float) LOWERBOUND_ALPHA;
			//v[i] = 0.0f; //TODO: Testweise
		}

		//
		// Save last gradient
		//
		lastGradient[i] = (float) grad;
	}

	@Override
	public double getLearningRate(UpdateParams u_i) {
		return alpha[u_i.i];
	}

	@Override
	public void postWeightUpdateTask(UpdateParams u_i) {
	}

	@Override
	public TDParams getBestParams(TDParams tdPar) {
		// TODO Auto-generated method stub
		return null;
	}

}

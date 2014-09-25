package adaptableLearningRates;

import nTupleTD.TDParams;
import nTupleTD.UpdateParams;

/**
 * Implementation of Suttons linear K1-algorithm and Schraudolphs non-linear
 * extension ELK1.
 * 
 * @author Markus Thill
 * 
 */
public class ELK1 extends LearningRates {

	private static final long serialVersionUID = -1507419996709546914L;
	public static final double K1_BEST_BETA_INIT = -4.7;
	public static final double K1_BEST_THETA = 6.0;
	public static final double ELK1_BEST_BETA_INIT = -4.5;
	public static final double ELK1_BEST_THETA = 5.0;

	// ##################################################################
	// K1, ELK1 variables
	private float[] k1_beta;
	private float[] h;

	/**
	 * Create all individual learning-rates according to the K1 or ELK1
	 * algorithm.
	 * 
	 * @param tdPar
	 *            User-defined parameters.
	 * @param length
	 *            Length of the corresponding weights-vector. For each weight
	 *            one learning rate is created.
	 */
	public ELK1(TDParams tdPar, int length) {
		super(null, tdPar); // no - for all weights common - elements
		createK1Tables(length);
	}

	// private double k1_updateBeta(int i, double delta) {
	// return Math.exp(k1_beta[i]);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see adaptableLearningRates.LearningRates#preWeightUpdateTask(nTupleTD.
	 * UpdateParams)
	 */
	@Override
	public void preWeightUpdateTask(UpdateParams u_i) {
		// cannot do anything here, because ALL beta_i have to be adjusted
		// first, before individual learning rates can be calculated!
		// This method is called weight for weight, so the learning-rate for w_i
		// cannot be known yet.
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
		// removed * u_i.derivY, since this factor is already coded in u_i.e_i
		double k_i = Math.exp(k1_beta[u_i.i]) * u_i.e_i * u_i.globalAlpha;
		return k_i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adaptableLearningRates.LearningRates#postWeightUpdateTask(nTupleTD.
	 * UpdateParams)
	 */
	@Override
	public void postWeightUpdateTask(UpdateParams u_i) {
		double k_i = getLearningRate(u_i);
		// removed * u_i.derivY, since this factor is already coded in u_i.e_i
		double hPlus = 1 - k_i * u_i.e_i;
		if (hPlus < 0.0)
			hPlus = 0.0;
		h[u_i.i] = (float) ((h[u_i.i] + k_i * u_i.delta) * hPlus);

	}

	/**
	 * @param i
	 *            Index of weight i.
	 * @param delta
	 *            Error-Signal.
	 * @param deriv
	 *            Derivation. 1 for K1 and (1-y^2) for tanh as activation
	 *            function.
	 * @param x_i
	 *            Input (of the feature vector) for index i.
	 */
	public void adjustBeta(int i, double delta, double deriv, double x_i) {
		double theta = tdPar.idbdTheta;
		double dBeta = theta * delta * x_i * h[i] * deriv;
		k1_beta[i] += dBeta;

		// bound beta
		if (k1_beta[i] < TDParams.BETA_LOWER_BOUND)
			k1_beta[i] = (float) TDParams.BETA_LOWER_BOUND;
		// if (k1_beta[i] > TDParams.BETA_UPPER_BOUND && USE_UPPER_BETA_BOUND)
		// k1_beta[i] = (float) TDParams.BETA_UPPER_BOUND;
	}

	/**
	 * @param length
	 *            Length of the corresponding weights-vector. For every weight a
	 *            single learning-rate is maintained.
	 */
	private void createK1Tables(int length) {
		k1_beta = new float[length];
		h = new float[length];
		for (int i = 0; i < length; i++) {
			k1_beta[i] = (float) tdPar.idbdBetaInit;
		}
	}

	/**
	 * Determine the individual stepsize alpha_i. This is e^(beta_i)
	 * 
	 * @param i
	 *            Index of weight i.
	 * @return The individual step-size alpha_i.
	 */
	public double getAlpha(int i) {
		return Math.exp(k1_beta[i]);
	}

	/* (non-Javadoc)
	 * @see adaptableLearningRates.LearningRates#getBestParams(nTupleTD.TDParams)
	 */
	@Override
	public TDParams getBestParams(final TDParams tdPar) {
		TDParams bestTDPar = null;
		try {
			bestTDPar = (TDParams) tdPar.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		switch (tdPar.updateMethod) {
		case ELK1:
			bestTDPar.idbdBetaInit = ELK1_BEST_BETA_INIT;
			bestTDPar.idbdTheta = ELK1_BEST_THETA;
			break;
		case K1:
			bestTDPar.idbdBetaInit = K1_BEST_BETA_INIT;
			bestTDPar.idbdTheta = K1_BEST_THETA;
			break;
		default:
			throw new UnsupportedOperationException("Wrong Method!!");
		}
		return bestTDPar;
	}

}

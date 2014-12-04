package adaptableLearningRates;

import nTupleTD.TDParams;
import nTupleTD.UpdateParams;
import nTupleTD.TDParams.UpdateMethod;

/**
 * Implementation of Suttons linear IDBD and Koop's non-linear IDBD for a
 * logistic activation function.
 * 
 * @author Markus Thill
 * 
 */
public class IDBD extends LearningRates {

	private static final long serialVersionUID = 4685813855032499613L;
	public static final double IDBD_LIN_BEST_BETA_INIT = -5.0;
	public static final double IDBD_LIN_BEST_THETA = 3.0;
	public static final double IDBD_NONLIN_BEST_BETA_INIT = -3.52;
	public static final double IDBD_NONLIN_BEST_THETA = 3.1;
	public static final double IDBD_MSE_BEST_BETA_INIT = -5.0;
	public static final double IDBD_MSE_BEST_THETA = 2.0;

	// ##################################################################
	// Incremental Delta-Bar-Delta Variables
	private float[] idbd_h; // has to be initialized with 0
	private float[] idbd_beta; // initialize as desired

	/**
	 * @param tdPar
	 *            TD-Params. Elements defined by the user, such as BETA, THETA
	 *            and more are needed for IDBD.
	 * @param length
	 *            Length of the corresponding weights-vector. For every weight a
	 *            learning-rate is maintained.
	 */
	public IDBD(TDParams tdPar, int length) {
		super(null, tdPar); // no - for all weights common - elements
		createIDBDTables(length);
	}

	/**
	 * Initialize the memory of IDBD.
	 * 
	 * @param length
	 */
	private void createIDBDTables(int length) {
		idbd_beta = new float[length];
		idbd_h = new float[length];
		for (int i = 0; i < length; i++) {
			idbd_beta[i] = (float) tdPar.idbdBetaInit;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adaptableLearningRates.LearningRates#preWeightUpdateTask(nTupleTD.
	 * UpdateParams)
	 */
	@Override
	public void preWeightUpdateTask(UpdateParams u_i) {
		int i = u_i.i;
		double idbd_theta = tdPar.idbdTheta;

		double deltaBeta = idbd_theta * u_i.delta * u_i.e_i * idbd_h[i];

		if (tdPar.updateMethod == UpdateMethod.IDBDMSE)
			deltaBeta = deltaBeta * u_i.derivY;

		if (Math.abs(deltaBeta) > TDParams.IDBD_BETA_CHANGE_MAX)
			deltaBeta = (deltaBeta < 0 ? -1 : +1)
					* TDParams.IDBD_BETA_CHANGE_MAX;
		idbd_beta[i] += deltaBeta;

		// bound beta
		if (idbd_beta[i] < TDParams.BETA_LOWER_BOUND)
			idbd_beta[i] = (float) TDParams.BETA_LOWER_BOUND;
		// if (idbd_beta[i] > TDParams.BETA_UPPER_BOUND && USE_UPPER_BETA_BOUND)
		// idbd_beta[i] = (float) TDParams.BETA_UPPER_BOUND;
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
		return Math.exp(idbd_beta[u_i.i]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adaptableLearningRates.LearningRates#postWeightUpdateTask(nTupleTD.
	 * UpdateParams)
	 */
	@Override
	public void postWeightUpdateTask(UpdateParams u_i) {
		int i = u_i.i;
		double e_i = u_i.e_i;
		double idbd_alpha = Math.exp(idbd_beta[i]);
		// Koops non-linear version needs *y*(1-y) in grad
		double x_plus = 1.0 - idbd_alpha * e_i * e_i * u_i.derivY;
		if (x_plus < 0)
			x_plus = 0.0;
		idbd_h[i] = (float) (idbd_h[i] * x_plus + idbd_alpha * u_i.delta * e_i);
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

		switch (tdPar.updateMethod) {
		case IDBD_LINEAR:
			bestTDPar.idbdBetaInit = IDBD_LIN_BEST_BETA_INIT;
			bestTDPar.idbdTheta = IDBD_LIN_BEST_THETA;
			break;
		case IDBD_NONLINEAR:
			bestTDPar.idbdBetaInit = IDBD_NONLIN_BEST_BETA_INIT;
			bestTDPar.idbdTheta = IDBD_NONLIN_BEST_THETA;
			break;
		case IDBDMSE:
			bestTDPar.idbdBetaInit = IDBD_MSE_BEST_BETA_INIT;
			bestTDPar.idbdTheta = IDBD_MSE_BEST_THETA;
			break;
		default:
			throw new UnsupportedOperationException("Wrong Method!!");
		}

		// Elig-Traces: we choose the variant [rr]. Resetting and replacing
		// traces with lambda = 0.8
		bestTDPar.lambda = 0.8;
		bestTDPar.replacingTraces = true;
		bestTDPar.resetEligOnRandomMove = true;

		return bestTDPar;
	}

}

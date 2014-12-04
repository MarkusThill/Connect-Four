package adaptableLearningRates;

import nTupleTD.TDParams;
import nTupleTD.UpdateParams;

/**
 * Wolfgang Konens non-linear extension of IDBD with tanh() as
 * activation-function.
 * 
 * @author Markus Thill
 * 
 */
public class IDBDwk extends LearningRates {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1225665101750973165L;
	public static final double IDBD_WK_BEST_BETA_INIT = -5.8;
	public static final double IDBD_WK_BEST_THETA = 1.0;
	public static final double IDBD_WK_BEST_OMEGA_K = 0.0;

	// ################################################
	// Incremental Delta-Bar-Delta Variables
	private float[] h; // has to be initialized with 0
	private float[] beta; // initialize as desired

	/**
	 * @param tdPar
	 *            User-defined parameters.
	 * @param length
	 *            Length of the corresponding weights-vector. For every weight a
	 *            learning-rate is maintained.
	 * @param com
	 *            For all weights of the system common parameters
	 */
	public IDBDwk(TDParams tdPar, int length, LRCommon com) {
		super(com, tdPar); // Set common parameters for all weights (k_acc,
							// GAMMA)
		createTables(length);
	}

	/**
	 * @param length
	 *            Length of the corresponding weights-vector. For every weight a
	 *            learning-rate is maintained.
	 */
	private void createTables(int length) {
		// Create tables for beta and h
		beta = new float[length];
		h = new float[length];
		for (int i = 0; i < length; i++) {
			beta[i] = (float) tdPar.idbdBetaInit;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * adaptableLearningRates.LearningRates#getWeightChange(nTupleTD.UpdateParams
	 * )
	 */
	@Override
	public double getWeightChange(UpdateParams u_i) {
		// Calculate delta w_i for the addressed weight i
		IDBDwkCommon com = (IDBDwkCommon) this.com;
		double a = getLearningRate(u_i); // alpha_i = e^beta_i
		double x_i = u_i.e_i;
		double Y = u_i.derivY; // Y = 1 - y^2
		double omega_k = com.omega_k;
		double k_acc = com.k_acc;
		double deltaW = a * Y * x_i * u_i.delta - omega_k * k_acc * x_i * x_i
				* u_i.w_i;
		return deltaW; // return the weight change
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adaptableLearningRates.LearningRates#preWeightUpdateTask(nTupleTD.
	 * UpdateParams)
	 */
	@Override
	public void preWeightUpdateTask(UpdateParams u_i) {
		// Called, before the weight i is updated
		int i = u_i.i;
		double idbd_theta = tdPar.idbdTheta;
		double Y = u_i.derivY;
		double deltaBeta = idbd_theta * Y * u_i.e_i * h[i] * u_i.delta;
		if (Math.abs(deltaBeta) > TDParams.IDBD_BETA_CHANGE_MAX)
			deltaBeta = (deltaBeta < 0 ? -1 : +1)
					* TDParams.IDBD_BETA_CHANGE_MAX;
		beta[i] += deltaBeta;

		// bound beta (lower bound)
		if (beta[i] < TDParams.BETA_LOWER_BOUND)
			beta[i] = (float) TDParams.BETA_LOWER_BOUND;
		// if (beta[i] > TDParams.BETA_UPPER_BOUND && USE_UPPER_BETA_BOUND)
		// beta[i] = (float) TDParams.BETA_UPPER_BOUND;
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
		// Calculate step-size for weight i
		return Math.exp(beta[u_i.i]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adaptableLearningRates.LearningRates#postWeightUpdateTask(nTupleTD.
	 * UpdateParams)
	 */
	@Override
	public void postWeightUpdateTask(UpdateParams u_i) {
		// Called after weight-update is performed
		IDBDwkCommon com = (IDBDwkCommon) this.com;
		int i = u_i.i;
		double x_i = u_i.e_i;
		double a_i = getLearningRate(u_i);
		double delta = u_i.delta;
		double Y = u_i.derivY;
		double Z = Y + 2 * u_i.y * delta;
		double omega_k = com.omega_k;
		double k_acc = com.k_acc;
		double xPlus = 1.0 - (a_i * Y * Z + omega_k * k_acc) * x_i * x_i;
		if (xPlus < 0)
			xPlus = 0;
		h[i] = (float) (h[i] * xPlus + a_i * Y * x_i * delta);

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

		bestTDPar.idbdBetaInit = IDBD_WK_BEST_BETA_INIT;
		bestTDPar.idbdTheta = IDBD_WK_BEST_THETA;
		bestTDPar.idbdWKOmegak = IDBD_WK_BEST_OMEGA_K;

		// Elig-Traces: we choose the variant [rr]. Resetting and replacing
		// traces with lambda = 0.8
		bestTDPar.lambda = 0.8;
		bestTDPar.replacingTraces = true;
		bestTDPar.resetEligOnRandomMove = true;

		return bestTDPar;
	}

	/**
	 * @param tdPar
	 * @return A new set of common parameters for IDBD_WK
	 */
	public static LRCommon createCommonLR(TDParams tdPar) {
		return new IDBDwkCommon(tdPar);
	}

	/**
	 * Maintains two parameters, that are common for all weights of the system
	 * in IDBD_WK. The first parameter is k_acc, the second is omega_k (which is
	 * a constant). With omega_k = 0, we get the original IDBD-algorithm.
	 * 
	 * @author Markus Thill
	 * 
	 */
	public static class IDBDwkCommon implements LRCommon {
		private static final double GAMMA = 0.001;
		double k_acc = 0.0;
		double omega_k = 0.0;

		IDBDwkCommon(TDParams tdPar) {
			omega_k = tdPar.idbdWKOmegak;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * adaptableLearningRates.LearningRates.LRCommon#commonPreUpdateTask
		 * (nTupleTD.UpdateParams)
		 */
		@Override
		public void commonPreUpdateTask(UpdateParams u) {
			// Only called once in time step t
			double y = u.y;
			k_acc = (1.0 - GAMMA) * k_acc + GAMMA * y * y;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * adaptableLearningRates.LearningRates.LRCommon#commonGetLearningRate
		 * (nTupleTD.UpdateParams)
		 */
		@Override
		public double commonGetLearningRate(UpdateParams u_i) {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * adaptableLearningRates.LearningRates.LRCommon#commonPostUpdateTask
		 * (nTupleTD.UpdateParams)
		 */
		@Override
		public void commonPostUpdateTask(UpdateParams u_i) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "Common LR:\r\n k_acc: " + k_acc + "\r\n omega_k: "
					+ omega_k + "\r\n";
		}
	}

}

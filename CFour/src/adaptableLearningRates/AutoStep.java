package adaptableLearningRates;

import nTupleTD.TDParams;
import nTupleTD.UpdateParams;

/**
 * Implementation of Mahmood's Autostep algorithm based on Sutton's linear IDBD.
 * Until now, AutoStep is only defined for linear units.
 * 
 * @author Markus Thill
 * 
 */
public class AutoStep extends LearningRates {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3116799738019648312L;
	public static final double AUTOSTEP_BESTALPHA = 0.02;
	public static final double AUTOSTEP_BESTMU = 1e-3;

	private float[] auto_alpha;
	private float[] auto_h;
	private float[] auto_v;
	private float auto_mu;
	private float AUTO_TAU = (float) 1e4;

	public AutoStep(TDParams tdPar, int length) {
		super(null, tdPar); // no - for all weights common - elements
		auto_mu = (float) tdPar.idbdTheta;
		createAUTOSTEPTables(length);
	}

	/**
	 * @param length
	 *            Length of the corresponding weights-vector. For every weight a
	 *            single learning-rate is maintained.
	 */
	private void createAUTOSTEPTables(int length) {
		auto_alpha = new float[length];
		auto_h = new float[length];
		auto_v = new float[length];
		for (int i = 0; i < length; i++) {
			auto_alpha[i] = (float) tdPar.alphaInit;
		}
	}

	/**
	 * Update the Parameter v_i and alpha_i in the AutoStep-Algorithm. 
	 * @param i Index i.
	 * @param delta Error-Signal of the TD-Algorithm.
	 * @param x_i The input for weight i. 
	 * @return The new value for alpha_i.
	 */
	public double auto_update_v_alpha(int i, double delta, double x_i) {
		double v_max1 = Math.abs(delta * x_i * auto_h[i]);
		double v_max2 = auto_v[i] + 1.0 / AUTO_TAU * auto_alpha[i] * x_i * x_i
				* (v_max1 - auto_v[i]);
		double v_i = Math.max(v_max1, v_max2);
		if (v_i != 0.0) {
			double expo = auto_mu * delta * x_i * auto_h[i] / v_i;
			auto_alpha[i] = (float) (auto_alpha[i] * Math.exp(expo));
		}
		auto_v[i] = (float) v_i;
		return auto_alpha[i];
	}

	/* (non-Javadoc)
	 * @see adaptableLearningRates.LearningRates#getLearningRate(nTupleTD.UpdateParams)
	 */
	@Override
	public double getLearningRate(UpdateParams u_i) {
		return auto_alpha[u_i.i];
	}

	/* (non-Javadoc)
	 * @see adaptableLearningRates.LearningRates#preWeightUpdateTask(nTupleTD.UpdateParams)
	 */
	@Override
	public void preWeightUpdateTask(UpdateParams u_i) {
		// alpha = alpha / M
		auto_alpha[u_i.i] = (float) (auto_alpha[u_i.i] * u_i.globalAlpha);
	}

	/* (non-Javadoc)
	 * @see adaptableLearningRates.LearningRates#postWeightUpdateTask(nTupleTD.UpdateParams)
	 */
	@Override
	public void postWeightUpdateTask(UpdateParams u_i) {
		int i = u_i.i;
		double x_i = u_i.e_i;
		double alpha = auto_alpha[u_i.i];
		double x_plus = 1.0 - alpha * x_i * x_i;
		if (x_plus < 0)
			x_plus = 0.0;
		auto_h[i] = (float) (auto_h[i] * x_plus + alpha * u_i.delta * x_i);
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
		bestTDPar.alphaInit = AUTOSTEP_BESTALPHA;
		bestTDPar.alphaFinal = AUTOSTEP_BESTALPHA;
		bestTDPar.idbdTheta = AUTOSTEP_BESTMU;
		return bestTDPar;
	}

}

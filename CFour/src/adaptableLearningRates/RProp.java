package adaptableLearningRates;

import nTupleTD.TDParams;
import nTupleTD.UpdateParams;

public class RProp extends LearningRates  {
	private static final long serialVersionUID = -5736224225251308916L;

	/**
	 * The default zero tolerance.
	 */
	public static final double DEFAULT_ZERO_TOLERANCE = 0.00000000000000001;
	
	/**
	 * The POSITIVE ETA value. This is specified by the resilient propagation
	 * algorithm. This is the percentage by which the deltas are increased by if
	 * the partial derivative is greater than zero.
	 */
	public static final double POSITIVE_ETA = 1.2;//1.2;
	
	/**
	 * The NEGATIVE ETA value. This is specified by the resilient propagation
	 * algorithm. This is the percentage by which the deltas are increased by if
	 * the partial derivative is less than zero.
	 */
	public static final double NEGATIVE_ETA = 0.5;//0.5;
	
	/**
	 * The minimum delta value for a weight matrix value.
	 */
	public static final double DELTA_MIN = 1e-5;
	
	/**
	 * The starting update for a delta.
	 */
	public static final double DEFAULT_INITIAL_UPDATE = 0.001;
	
	/**
	 * The maximum amount a delta can reach.
	 */
	public static final double DEFAULT_MAX_STEP = 50;
	
	private final TDParams tdPar;
	
	private float[] lastWeightChange;
	
	/**
	 * The value error at the beginning of the previous training iteration. This
	 * value is compared with the error at the beginning of the current
	 * iteration to determine if the error has improved.
	 */
	private double lastError = Double.POSITIVE_INFINITY;
	
	public boolean improved = true;
	
	public boolean plusVariant = true;

	/**
	 * The learning step sizes (\Delta_ij) for the weights w_ij from the
	 * previous iteration
	 */
	private float[] lastDelta;

	/**
	 * The gradient of the loss function for the weights w_ij from the previous
	 * iteration
	 */
	private float[] lastGradient;

	public RProp(TDParams tdPar, int length) {
		super(null, tdPar); // no - for all weights common - elements
		this.tdPar = tdPar;
		createTables(length);
	}
	
	private void createTables(int length) {
		lastWeightChange = new float[length];
		lastDelta = new float[length];
		lastGradient = new float[length];
		
		// initialize delta
		for(int i=0;i<lastDelta.length;i++)
			lastDelta[i] = (float) DEFAULT_INITIAL_UPDATE;
	}
	
	private double preWeightUpdatePlus(UpdateParams u_i, double Et) {
		final int i = u_i.i;
		
		double grad_i_Et = -2.0 * u_i.e_i * u_i.delta;
		final int change = (int) Math.signum(grad_i_Et * lastGradient[i]);
		double weightChange = 0;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			double delta = lastDelta[i] * POSITIVE_ETA;
			delta = Math.min(delta, DEFAULT_MAX_STEP);
			// /WK/ added a "-" in front of Math.signum
			weightChange = -Math.signum(grad_i_Et) * delta;
			lastDelta[i] = (float) delta;
			lastGradient[i] = (float) grad_i_Et;
		} else if (change < 0) {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			double delta = lastDelta[i] * NEGATIVE_ETA;
			delta = Math.max(delta, DELTA_MIN);
			lastDelta[i] = (float) delta;

			if (!improved || Et > lastError) {
				weightChange = -lastWeightChange[i];
			}

			// Set the previous gradient to zero so that there will be no
			// step size adjustment in the next iteration
			lastGradient[i] = 0f;
		} else if (change == 0) {
			// if change==0 then there is no change to the delta
			final double delta = lastDelta[i];
			weightChange = -Math.signum(grad_i_Et) * delta;
			lastGradient[i] = (float) grad_i_Et;
		}
		return weightChange;
	}

	private double preWeightUpdateMinus(UpdateParams u_i, double Et) {
		final int i = u_i.i;

		double grad_i_Et = -2.0 * u_i.e_i * u_i.delta;
		final int change = (int) Math.signum(grad_i_Et * lastGradient[i]);
		double weightChange = 0;
		
		double delta = lastDelta[i];		// /WK/ Bug fix: this is for the case change==0
		//grad_i_Et: double gIndex = gradients[index];
		
		if (change > 0) {
			delta = lastDelta[i]* POSITIVE_ETA;
			delta = Math.min(delta, DEFAULT_MAX_STEP);			
		} else if (change < 0) {						// /WK/ Bug fix: exclude here change==0
			// if change<0, then the sign has changed, and the last
			// delta was too big
			delta = lastDelta[i] * NEGATIVE_ETA;
			delta = Math.max(delta, DELTA_MIN);
			if (improved) grad_i_Et = 0.0;				// /WK/ changed w.r.t. Encog !
			// this has two effects: (1) no weight change in the current iteration AND
			// (2) weight change with this delta in the next iteration (where lastGradient[index]=0)
		}
		//
		// in case change==0 we do only weightChange, no step size change 
		//
		lastGradient[i] = (float) grad_i_Et;
		weightChange = -Math.signum(grad_i_Et) * delta;
		lastDelta[i] = (float) delta;
		
		return weightChange;
	}
	
	
	@Override
	public void preWeightUpdateTask(UpdateParams u_i) {
		double Et = u_i.delta * u_i.delta;
		double weightChange = 0;
		if (plusVariant)
			weightChange = preWeightUpdatePlus(u_i, Et);
		else
			weightChange = preWeightUpdateMinus(u_i, Et);
		lastError = Et;
		lastWeightChange[u_i.i] = (float) weightChange;
		
	}

	@Override
	public double getLearningRate(UpdateParams u_i) {
		return lastDelta[u_i.i];
	}
	
	@Override
	public double getWeightChange(UpdateParams u_i) {
		return lastWeightChange[u_i.i];
	}

	@Override
	public void postWeightUpdateTask(UpdateParams u_i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TDParams getBestParams(TDParams tdPar) {
		return null;
	}

	public TDParams getTdPar() {
		return tdPar;
	}

}

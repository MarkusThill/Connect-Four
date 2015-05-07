package adaptableLearningRates;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import nTupleTD.TDParams;
import nTupleTD.UpdateParams;

/**
 * Implementation of the iRProp+ algorithm.
 * @author Markus Thill
 *
 */
public class IRprop extends LearningRates {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4663145721666613436L;
	// ---------------------------------------------------------------------
	// DEBUG
	private static final boolean DEBUG = false;
	private long dbg_counterWeightUpdates;
	private long dbg_counterGradMultNeq0;
	// ---------------------------------------------------------------------

	// With elig-traces, this list can get larger....
	private TreeMap<Integer, Double> lastWeightUpdate;
	private TreeMap<Integer, Double> currentWeightUpdate;
	private TreeSet<Integer> setGradZero; // Used between transition of
											// time-steps

	private  boolean USE_LAST_CHANGE = true;
	private TreeMap<Integer, Double> lastDelta;

	private float[] alpha;

	public IRprop(TDParams tdPar, int length, LRCommon com) {
		super(com, tdPar);
		createTables(length);
		// has to be set, to ensure that overridden method PostUpdateTask is
		// called
		doPostUpdateTask = true;
	}

	/**
	 * @param length  Length of the corresponding weights-vector. For every weight a
	 *            learning-rate is maintained.
	 */
	private void createTables(int length) {
		lastWeightUpdate = new TreeMap<Integer, Double>();
		currentWeightUpdate = new TreeMap<Integer, Double>();
		setGradZero = new TreeSet<Integer>();
		lastDelta = (USE_LAST_CHANGE ? new TreeMap<Integer, Double>() : null);

		alpha = new float[length];
		for (int i = 0; i < length; i++) {
			alpha[i] = (float) tdPar.alphaInit;
		}
	}

	/* (non-Javadoc)
	 * @see adaptableLearningRates.LearningRates#getWeightChange(nTupleTD.UpdateParams)
	 */
	@Override
	public double getWeightChange(UpdateParams u_i) {
		if (DEBUG) {
			assert (currentWeightUpdate.containsKey(u_i.i));
		}
		return currentWeightUpdate.get(u_i.i);
	}

	/* (non-Javadoc)
	 * @see adaptableLearningRates.LearningRates#preWeightUpdateTask(nTupleTD.UpdateParams)
	 */
	@Override
	public void preWeightUpdateTask(UpdateParams u_i) {
		IRpropCommon com = (IRpropCommon) this.com;
		int i = u_i.i;
		double currentDelta = u_i.delta;
		double lastDelta = com.lastDelta;
		double etaP = com.etaPlus;
		double etaN = com.etaMinus;
		double dW = 0.0;
		
		if(USE_LAST_CHANGE) {
			Double lastDeltaV = this.lastDelta.get(i);
			lastDelta = (lastDeltaV == null ? 0.0 : lastDeltaV);
			// This current delta will be the lastDelta next time
			this.lastDelta.put(i, currentDelta);
		}
		
		// Assume that currentWeightUpdate is empty when we move to next
		// time-step
		// Add this current index to the list
		if (DEBUG) {
			// currentWeightUpdate is not supposed to contain this index
			assert (currentWeightUpdate.containsKey(i) == false);
		}

		// Check if index is in list for time t-1
		double gradMult = 0.0;
		if (USE_LAST_CHANGE || lastWeightUpdate.containsKey(i))
			gradMult = lastDelta * currentDelta;

		// --------------------------------------------------------------------------
		// DEBUG
		// Check, in how many cases the multiplication of gradient g_i and g_i'
		// is unequal zero
		if (DEBUG) {
			dbg_counterWeightUpdates++;
			if (gradMult != 0.0) {
				dbg_counterGradMultNeq0++;
				if (dbg_counterGradMultNeq0 % 100000 == 0)
					System.out.println("Ratio of g_i * g_i' <> 0 :"
							+ (double) dbg_counterGradMultNeq0
							/ dbg_counterWeightUpdates);
			}
		}
		// --------------------------------------------------------------------------

		// Check whether g_i * g_i' > 0
		if (gradMult > 0.0) {
			// same direction as last time (g_i * g_i' > 0)
			// adjust step-size alpha first
			alpha[i] = (float) Math
					.min(alpha[i] * etaP, IRpropCommon.ALPHA_MAX);

			// Determine weight-change
			dW = Math.signum(currentDelta) * alpha[i];
		} else if (gradMult < 0.0) {
			// change of direction (g_i * g_i' < 0)
			// adjust step-size alpha first
			alpha[i] = (float) Math
					.max(alpha[i] * etaN, IRpropCommon.ALPHA_MIN);

			// Calculate MSE
			double currentMSE = currentDelta * currentDelta;
			double lastMSE = lastDelta * lastDelta;
			if (currentMSE > IRpropCommon.GAMMA * lastMSE) {
				// If MSE is larger now, than last step, go back in opposite
				// direction
				// Subtract last recommended weight change
				Double lastWeightUp = lastWeightUpdate.get(i);
				dW = -(lastWeightUp == null ? 0 : lastWeightUp);
			} else
				dW = 0.0;

			// Gradient g_i is assumed to be zero next time step
			setGradZero.add(i);
			if(USE_LAST_CHANGE) // make sure that gradMult is zero next time
				this.lastDelta.put(i, 0.0);
		} else {
			// g_i * g_i' = 0
			dW = Math.signum(currentDelta) * alpha[i];
		}
		currentWeightUpdate.put(i, dW);
	}

	/* (non-Javadoc)
	 * @see adaptableLearningRates.LearningRates#getLearningRate(nTupleTD.UpdateParams)
	 */
	@Override
	public double getLearningRate(UpdateParams u_i) {
		// Generally, not needed...
		return alpha[u_i.i];
	}

	/* (non-Javadoc)
	 * @see adaptableLearningRates.LearningRates#postWeightUpdateTask(nTupleTD.UpdateParams)
	 */
	@Override
	public void postWeightUpdateTask(UpdateParams u_i) {
		// Nothing to do
	}

	/* (non-Javadoc)
	 * @see adaptableLearningRates.LearningRates#getBestParams(nTupleTD.TDParams)
	 */
	@Override
	public TDParams getBestParams(TDParams tdPar) {
		TDParams bestTDPar = null;
		try {
			bestTDPar = (TDParams) tdPar.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return bestTDPar;
	}

	/* (non-Javadoc)
	 * @see adaptableLearningRates.LearningRates#postUpdateTask(boolean)
	 */
	@Override
	public void postUpdateTask(boolean gameFinished) {
		if (!gameFinished) {
			lastWeightUpdate = currentWeightUpdate;
			currentWeightUpdate = new TreeMap<Integer, Double>();
			Set<Integer> c = setGradZero;

			// Remove entrys for which g_i was set to zero
			for (Iterator<Integer> i = c.iterator(); i.hasNext();) {
				lastWeightUpdate.remove(i.next());
			}

			setGradZero.clear();
		} else { // Game finished, Reset everything
			IRpropCommon com = (IRpropCommon) this.com;
			com.lastDelta = 0.0;
			lastWeightUpdate = new TreeMap<Integer, Double>();
			currentWeightUpdate = new TreeMap<Integer, Double>();
			setGradZero = new TreeSet<Integer>();
		}

	}

	/**
	 * @param tdPar
	 * @return
	 */
	public static LRCommon createCommonLR(TDParams tdPar) {
		return new IRpropCommon();
	}

	/**
	 * @author Markus Thill
	 *
	 */
	public static class IRpropCommon implements LRCommon {
		private static final double ALPHA_MAX = 0.1;
		private static final double ALPHA_MIN = 1e-7;
		private static final double GAMMA = 1.05;
		private double lastDelta = 0.0;
		private double etaPlus =  1.2;
		private double etaMinus = 0.8;

		@Override
		public void commonPreUpdateTask(UpdateParams u_i) {
			// Nothing to do
		}

		@Override
		public double commonGetLearningRate(UpdateParams u_i) {
			// Not needed here
			return -99999.999;
		}

		@Override
		public void commonPostUpdateTask(UpdateParams u_i) {
			lastDelta = u_i.delta;
		}
	}
}

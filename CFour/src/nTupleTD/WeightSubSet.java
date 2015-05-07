package nTupleTD;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import nTupleTD.TDParams.UpdateMethod;

import adaptableLearningRates.*;
import adaptableLearningRates.LearningRates.LRCommon;

/**
 * A WeightSubSet is a subset of weights of a value-function, assuming that the
 * feature-vector of this subset always only returns one element unequal zero.
 * 
 * @author Markus Thill
 * 
 */
public abstract class WeightSubSet implements Serializable {
	private static final boolean DEBUG = false;
	private static final int DBG_SIZE_RWC_LIST = 100000;
	private static final long serialVersionUID = 8718866390489881056L;
	private static final int SIZEOF_FLOAT = Float.SIZE;
	private double EPS = 0.001;
	protected TDParams tdPar = null;
	protected float lut[];
	protected LearningRates lr;
	protected final LRCommon lrCommon; // final, to make sure, it is set
										// directly in the beginning
	protected boolean useElig = true;
	protected TreeMap<Integer, EligibilityTrace> eligTraces = null;

	public static class EligibilityTrace implements Serializable {
		private static final long serialVersionUID = 9033762045461508842L;
		double e_i;
		double x_i; // Set to zero on the first scale. Only set again, if weight
					// is activated...

		public EligibilityTrace() {
			this.e_i = 0.0;
			this.x_i = 0.0;
		}

		public EligibilityTrace(double e_i, double x_i) {
			this.e_i = e_i;
			this.x_i = x_i;
		}
	}

	private ArrayList<Double> dbgRWCList = DEBUG ? new ArrayList<Double>(
			DBG_SIZE_RWC_LIST) : null;

	// ======================================================================
	// Abstract Methods
	// ======================================================================
	/**
	 * Assume for the following methods, that every board only creates one
	 * feature, so that only one weight will be activated by the input-vector
	 * 
	 * @param board
	 * @return
	 */
	public abstract int getFeature(int[][] board);

	/**
	 * {@link WeightSubSet#getFeature(int[][])}
	 * 
	 * @param board
	 * @param mirror
	 * @return
	 */
	public abstract int getFeature(int[][] board, boolean mirror);

	/**
	 * {@link WeightSubSet#getFeature(int[][])}
	 * 
	 * @param fieldP1
	 * @param fieldP2
	 * @return
	 */
	public abstract int getFeature(long fieldP1, long fieldP2);

	protected abstract void createLUTandLR();

	public WeightSubSet(LRCommon lrCommon) {
		this.lrCommon = lrCommon;
	}

	protected LearningRates createLearningRates(int length) {
		UpdateMethod m = tdPar.updateMethod;
		switch (m) {
		case AUTOSTEP:
			return new AutoStep(tdPar, length);
		case IDBD_LINEAR:
		case IDBD_NONLINEAR:
			return new IDBD(tdPar, length);
		case K1:
		case ELK1:
			return new ELK1(tdPar, length);
		case TCL:
			return new TemporalCoherence(tdPar, length, this);
		case TDL:
			return new STDLearningRate(tdPar);
		case IDBD_WK:
			return new IDBDwk(tdPar, length, lrCommon); // lrCommon must already
														// exist (it does,
														// because it is final)
		case ALPHABOUND:
			return new AlphaBound(tdPar);
		case IDBDMSE:
			return new IDBD(tdPar, length);
		case IRPROP_PLUS:
			// return new IRprop(tdPar, length, lrCommon);
			return new RProp(tdPar, length);
		case ALAP:
			return new ALAP(tdPar, length);
		case SMD:
			return new SMD(tdPar, length);
		default:
			throw new UnsupportedOperationException("Method not supported yet!");
		}
	}

	/**
	 * @param random
	 *            true, if all weights shall be initialized randomly
	 */
	public void initWeights(boolean random) {
		Random rand = new Random();
		for (int i = 0; i < lut.length; i++)
			lut[i] = (float) (random ? EPS * (rand.nextDouble() * 2 - 1) : 0.0f);
	}

	/**
	 * @param fieldP1
	 *            Bit-Board-representation of player ones field
	 * @param fieldP2
	 *            Bit-Board-representation of player twos field
	 * @return Score (not necessarily in range -1 .. +1)
	 */
	public double getWeight(long fieldP1, long fieldP2) {
		int i = getFeature(fieldP1, fieldP2);
		return lut[i];
	}

	public double getWeight(int i) {
		return lut[i];
	}

	// public void update(int index, double globalAlpha, double delta,
	// double grad, double x_i) {
	// use this method, when lambda = 0
	// in this case elig = grad
	// update(index, globalAlpha, delta, grad, grad, x_i);
	// }

	public void update(UpdateParams u_i) {
		lr.preWeightUpdateTask(u_i);
		// Some methods need the current weight-value
		u_i.w_i = lut[u_i.i];
		double dW = lr.getWeightChange(u_i);
		if (DEBUG) {
			// add rwc to list
			dbgRWCList.add(dW);
			if (dbgRWCList.size() == DBG_SIZE_RWC_LIST) {
				Double sum = 0.0;
				for (Double i : dbgRWCList) {
					sum += Math.abs(i);
				}
				System.out.println("average abs. rwc in episode: " + sum
						/ DBG_SIZE_RWC_LIST);
				dbgRWCList.clear();
			}
		}
		lut[u_i.i] += dW;
		lr.postWeightUpdateTask(u_i);
	}

	public void updateLUTwithElig(UpdateParams u) {
		Set<Entry<Integer, EligibilityTrace>> c = eligTraces.entrySet();
		for (Iterator<Entry<Integer, EligibilityTrace>> i = c.iterator(); i
				.hasNext();) {
			Entry<Integer, EligibilityTrace> entry = i.next();

			int index = entry.getKey();
			EligibilityTrace et = entry.getValue();
			double e_i = et.e_i;
			double x_i = et.x_i;
			UpdateParams u_i = new UpdateParams(index, u.globalAlpha, u.delta,
					u.derivY, e_i, x_i, u.y);
			update(u_i);
		}
	}

	public void resetElig() {
		eligTraces.clear();
	}

	public void addGradToElig(int index, double grad, double x_i) {
		boolean replacingTraces = tdPar.replacingTraces;
		Object entry = eligTraces.get(index);
		if (!replacingTraces) {
			// Before, we also replaced when we had x_i=2, because we called
			// this function twice. See what changes now...
			EligibilityTrace et = (EligibilityTrace) (entry == null ? new EligibilityTrace()
					: entry);

			et.e_i += grad;
			et.x_i = x_i;
			eligTraces.put(index, et);
		} else
			eligTraces.put(index, new EligibilityTrace(grad, x_i));
	}

	public void scaleElig(double lambdaGamma) {
		// Multiply all elements of the elig-vector with lambda*gamma
		Set<Entry<Integer, EligibilityTrace>> c = eligTraces.entrySet();
		for (Iterator<Entry<Integer, EligibilityTrace>> i = c.iterator(); i.hasNext();) {
			Entry<Integer, EligibilityTrace> entry = i.next();
			EligibilityTrace et = entry.getValue();
			et.e_i *= lambdaGamma;
			//
			// We set x_i to zero, since it may not be activated
			// anymore in this episode. Otherwise x_i will be set again...
			//
			et.x_i = 0.0;
			entry.setValue(et);
		}
	}

	/**
	 * @param i
	 *            Vector, activating a certain elig-trace.
	 * @return the activated elig-trace.
	 */
	public double getElig(int i) {
		Double value = eligTraces.get(i).e_i;
		return (value == null ? 0.0 : value);
	}

	public int countActiveEligTraces() {
		if (eligTraces != null) {
			return eligTraces.size();
		}
		return 0;
	}

	/**
	 * @param inBytes
	 *            true, if LUT-Size shall be returned in Bytes
	 * @return Number of Elements in the LUT or the Size in Bytes
	 */
	public int getLUTSize(boolean inBytes) {
		if (lut != null)
			return lut.length * (inBytes ? SIZEOF_FLOAT : 1);
		return 0;
	}
	
	public float[] getLUT() {
		return lut;
	}

	public void printLUT() {
		double biggest = Double.NEGATIVE_INFINITY;
		double smallest = Double.POSITIVE_INFINITY;
		for (int i = 0; i < lut.length; i++)
			if (lut[i] != 0.0) {
				System.out.println(i + "; " + lut[i]);
				if (lut[i] < smallest)
					smallest = lut[i];
				if (lut[i] > biggest)
					biggest = lut[i];
			}
		System.out.println("biggest" + "; " + biggest);
		System.out.println("smallest" + "; " + smallest);
	}

}
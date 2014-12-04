package adaptableLearningRates;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import nTupleTD.TDParams;
import nTupleTD.UpdateParams;
import nTupleTD.WeightSubSet;

public class TemporalCoherence extends LearningRates {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6074042469937964271L;
	// ##################################################################
	// Temporal Coherence (TCL) Variables
	private float tcN[] = null;
	private float tcA[] = null;
	private TreeMap<Integer, Double> tclAccuR = null;

	private WeightSubSet parentWeights;

	public TemporalCoherence(TDParams tdPar, int length, WeightSubSet parent) {
		super(null, tdPar); // no - for all weights common - elements
		parentWeights = parent;
		createTCLTables(length);
	}

	private void createTCLTables(int length) {
		tcN = new float[length];
		tcA = new float[length];
		if (tdPar.tclUseUpdateEpisodes)
			tclAccuR = new TreeMap<Integer, Double>();
	}

	@Override
	public void preWeightUpdateTask(UpdateParams u_i) {
		final boolean updateLUTFirst = !tdPar.tclUpdate1WeightFactors2Weights;
		if (!updateLUTFirst)
			updateTables(u_i);
	}

	@Override
	public void postWeightUpdateTask(UpdateParams u_i) {
		final boolean updateLUTFirst = !tdPar.tclUpdate1WeightFactors2Weights;
		if (updateLUTFirst)
			updateTables(u_i);

		if (tdPar.tclUseUpdateEpisodes)
			// Accumulated rwc is not needed anymore after the episode
			tclAccuR.clear();
	}

	@Override
	public double getLearningRate(UpdateParams u_i) {
		int i = u_i.i;
		double tc = (tcA[i] == 0 ? 1.0 : Math.abs(tcN[i]) / tcA[i]);

		// When choosing exp. scheme of tcFactor
		if (tdPar.tclUseExpScheme) {
			double facA = tdPar.tclExpSchemeFacA;
			tc = Math.exp(facA * (tc - 1.0));

			// piecewise linear
			// double bord = Math.exp(-facA);
			// tc = facA*tc + 1 - facA;
			// if(tc < bord)
			// tc = bord;
		}
		return tc * u_i.globalAlpha;
	}

	@Override
	public TDParams getBestParams(final TDParams tdPar) {
		TDParams bestTDPar = null;
		try {
			bestTDPar = (TDParams) tdPar.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		bestTDPar.alphaInit = 0.05;
		bestTDPar.alphaFinal = 0.05;
		bestTDPar.tclUseUpdateEpisodes = false;
		bestTDPar.tclUseExpScheme = true;
		bestTDPar.tclExpSchemeFacA = 2.7;
		bestTDPar.tclMuInit = 1.0;
		bestTDPar.tclUpdate1WeightFactors2Weights = false;
		bestTDPar.tclUseErrorSignal = false;

		// Elig-Traces: we choose the variant [rr]. Resetting and replacing
		// traces with lambda = 0.8
		bestTDPar.lambda = 0.8;
		bestTDPar.replacingTraces = true;
		bestTDPar.resetEligOnRandomMove = true;

		return bestTDPar;
	}

	private void updateTables(UpdateParams u_i) {
		// Recommended change or delta
		int i = u_i.i;
		final boolean useRWC = !tdPar.tclUseErrorSignal;
		final double rwc = (useRWC ? u_i.globalAlpha * u_i.delta * u_i.e_i
				: u_i.delta);
		final double mu = tdPar.tclMuInit;

		// update tables
		tcN[i] = (float) (mu * tcN[i] + rwc);
		tcA[i] = (float) (mu * tcA[i] + Math.abs(rwc));
	}

	public void accuRecWeightChange(int index, double recWeightChange) {
		Object entry = tclAccuR.get(index);
		double value = (Double) (entry == null ? 0.0 : entry);
		tclAccuR.put(index, value + recWeightChange);
	}

	public void tclEpsisodeUpdate(double alpha) {
		Set<Entry<Integer, Double>> c = tclAccuR.entrySet();
		for (Iterator<Entry<Integer, Double>> e = c.iterator(); e.hasNext();) {
			Entry<Integer, Double> entry = e.next();

			int i = entry.getKey();
			double rwc = entry.getValue();
			// remember: lut[i] += stepSize * delta * elig;
			// y is not needed, so set to -999.9
			// e_i is already in rwc, so e_i = 1
			UpdateParams u_i = new UpdateParams(i, alpha, rwc, 1.0, 1.0, -999.9);
			parentWeights.update(u_i);
		}
	}

}

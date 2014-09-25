package nTupleTD;

import java.io.Serializable;
import java.text.DecimalFormat;

import nTupleTD.TDParams.Activation;
import nTupleTD.TDParams.UpdateMethod;

import adaptableLearningRates.ActivationFunction;
import adaptableLearningRates.AutoStep;
import adaptableLearningRates.ELK1;
import adaptableLearningRates.IDBDwk;
import adaptableLearningRates.IRprop;
import adaptableLearningRates.LearningRates.LRCommon;
import adaptableLearningRates.TemporalCoherence;
import c4.ConnectFour;

/**
 * @author Markus Thill
 * 
 *         Implementation of an learning value-function using a n-Tuple-System
 * 
 */
public class ValueFuncC4 implements Serializable {

	private static final long serialVersionUID = -416104669927543063L;

	// Number of columns in a c4-board
	private static final int NUMCOLS = 7;

	// Number of Rows in a c4-board
	private static final int NUMROWS = 6;

	// use if feature vector elements are max. g_i(s_t) = 1
	private static final boolean USEBINARYFV = false;

	private TDParams tdPar = null;

	// Global learning rate
	// Adjusted during the training
	private double alpha;

	// Usage: exponential decrease
	private double alphaChangeRatio;

	// Array containing all nTuple-Objects
	private NTupleC4 nTuples[][];

	// All weights of the system, including bias weight
	private WeightSubSet weights[][];

	// For all weights of the system common structures and components regarding
	// the adaptable learning rates.
	LRCommon lrCommon = null;

	// All learning-rates for the weights of the system
	// LearningRates lr[][];

	// Transposition-Table
	private int transPosSize = 4194304 / 128;
	private long trKey[] = null;
	private int trIndexList[][] = null;

	// TCL-Episode-Counter
	private long episodeCounter = 0;

	/**
	 * Generate a random set of n-Tuples
	 */
	public ValueFuncC4(TDParams tdPar) {
		if (!tdPar.randNTuples) {
			initNTuples(tdPar.nTuples, tdPar, false);
		} else {
			Integer[][] nTuples = null;
			int mode = tdPar.randMode;
			int tupleLen = tdPar.randTupleLen;
			int tupleNum = tdPar.tupleNum;
			switch (mode) {
			case 0: // random sample-points, length of all tuples equal
				nTuples = NTupleFactory.generateRandomTuples(tupleLen,
						tupleNum, false);
				break;
			case 1: // random sample-points, length of all tuples random
					// (2 .. tupleLen)
				nTuples = NTupleFactory.generateRandomTuples(tupleLen,
						tupleNum, true);
				break;
			case 2: // Random Walk, length of all tuples equal
				nTuples = NTupleFactory.generateRandomWalks(tupleLen, tupleNum,
						false);
				break;
			case 3: // Random Walk, length of all tuples random (2 .. tupleLen)
				nTuples = NTupleFactory.generateRandomWalks(tupleLen, tupleNum,
						true);
				break;
			}
			initNTuples(nTuples, tdPar, true);
			// Refresh N-tuples in tdPar-Object
			this.tdPar.nTuples = getNTuples1Dim();
		}
	}

	/**
	 * @param nTuples
	 *            The n-tuples can be given to this Constructor as arrays of
	 *            different format<br>
	 *            <b>Long[][]</b>: List of the n-Tuples. Every single n-Tuple
	 *            consists of long-masks (check bitboard-representation for
	 *            further information)<br>
	 *            <b>Long[]</b>: List of the n-Tuples. Each n-Tuple is coded in
	 *            one long-variable (check bitboard-representation for further
	 *            information) <br>
	 *            <b>Integer[][]</b>: List of the n-Tuples. Each n-Tuple
	 *            consists of the selected field-numbers (0-41) <br>
	 *            <b>Integer[][][]</b>: List of the n-Tuples. each n-Tuple
	 *            consists of sampling points in the form (col, row)
	 * 
	 * @param tdPar
	 */
	public ValueFuncC4(Object[] nTuples, TDParams tdPar) {
		initNTuples(nTuples, tdPar, false);
	}

	private void initNTuples(Object[] nTuples, TDParams tdPar,
			boolean randomlyCreated) {
		if (nTuples.length != tdPar.tupleNum)
			throw new UnsupportedOperationException(
					"Number of NTuples in TDParams-Object and in N-Tuple-List differ!");
		if (randomlyCreated != tdPar.randNTuples)
			throw new UnsupportedOperationException(
					"Wrong Usage of Constructor. Do not set TDParams.randNTuples to true if you give a list of n-tuples to the constructor");

		this.tdPar = tdPar;

		// The last element will be the bias-weight for both players
		int numTuples = nTuples.length;
		this.nTuples = new NTupleC4[2][numTuples];

		if (!this.tdPar.useHashExtern && !this.tdPar.useHashIntern)
			this.tdPar.hashSize = 0;

		// In certain cases, all weights of the system have common components in
		// the Step-Size Adaption algorithm. Create here
		if (tdPar.updateMethod == UpdateMethod.IDBD_WK)
			lrCommon = IDBDwk.createCommonLR(tdPar);
		else if(tdPar.updateMethod == UpdateMethod.IRPROP_PLUS)
			lrCommon = IRprop.createCommonLR(tdPar);

		for (int i = 0; i < nTuples.length; i++) {
			this.nTuples[0][i] = new NTupleC4(nTuples[i], this.tdPar, lrCommon);
			this.nTuples[1][i] = new NTupleC4(nTuples[i], this.tdPar, lrCommon);
		}

		// Initialize weights
		weights = new WeightSubSet[2][numTuples + (tdPar.useBiasWeight ? 1 : 0)];
		for (int i = 0; i < nTuples.length; i++) {
			weights[0][i] = this.nTuples[0][i];
			weights[1][i] = this.nTuples[1][i];
		}

		// Add bias-weights, if necessary
		if (tdPar.useBiasWeight) {
			weights[0][numTuples] = createBiasWeight();
			weights[1][numTuples] = createBiasWeight();
		}

		// Initialize all learning rates (step size parameters) of the system
		// lr = new LearningRates[2][weights[0].length];
		// for (int i = 0; i < lr[0].length; i++) {
		// int lutsize = weights[0][i].getLUTSize(false);
		// lr[0][i] = createLearningRates(lutsize);
		// lutsize = weights[1][i].getLUTSize(false);
		// lr[1][i] = createLearningRates(lutsize);
		// }

		initAlpha(tdPar);
		initHash(this.tdPar.hashSize);
		episodeCounter = 0;
	}

	private void initAlpha(TDParams tdPar) {
		double aFin = tdPar.alphaFinal;
		double aInit = tdPar.alphaInit;
		double numGames = tdPar.trainGameNum;
		alpha = tdPar.alphaInit;
		alphaChangeRatio = Math.pow(aFin / aInit, 1.0 / numGames);
	}

	private WeightSubSet createBiasWeight() {
		// Bias is simply a system with one weight!!
		return new WeightSubSet(null) {
			private static final long serialVersionUID = 1514090056283795170L;
			{
				createLUTandLR();
			}

			@Override
			public int getFeature(long fieldP1, long fieldP2) {
				return 0;
			}

			@Override
			public int getFeature(int[][] board, boolean mirror) {
				return 0;
			}

			@Override
			public int getFeature(int[][] board) {
				return 0;
			}

			@Override
			protected void createLUTandLR() {
				lut = new float[1];
				lr = createLearningRates(1);
			}
		};
	}

	/**
	 * adjust the learning-rate alpha
	 */
	public void updateAlpha() {
		if(tdPar.updateMethod == UpdateMethod.TDL)
			alpha = alpha * alphaChangeRatio;
	}

	/**
	 * @param field
	 *            bit-boards of both players
	 * @return the mirrored bit-boards of both players (mirrored at
	 *         center-column)
	 */
	private long[] mirror(long[] field) {
		long[] mirrored = new long[2];
		mirrored[0] = ConnectFour.getMirroredField(field[0]);
		mirrored[1] = ConnectFour.getMirroredField(field[1]);
		return mirrored;
	}

	/**
	 * Update the weights of all n-Tuples
	 * 
	 * @param curPlayer
	 *            current player. Needed for the divided LUT
	 * @param curZobr
	 *            Zobrist Key of the current board. For curZobr=-1 the Index-Set
	 *            to the current board will be calculated and not retreived from
	 *            the hash-table
	 * @param nextZobr
	 *            Zobrist Key of the next board. For nextZobr=-1 the Index-Set
	 *            to the next board will be calculated and not retreived from
	 *            the hash-table.
	 * @param curBoard
	 *            Current Board
	 * @param nextBoard
	 *            Next board
	 * @param finished
	 *            true, if game is over
	 * @param reward
	 *            reward
	 */
	public void updateWeights(int curPlayer, long curZobr, long nextZobr,
			long[] curBoard, long[] nextBoard, boolean finished, double reward) {
		double y, tg;

		double gamma = tdPar.gamma;
		UpdateMethod updateMeth = tdPar.updateMethod;
		boolean useEpisodes = tdPar.tclUseUpdateEpisodes;
		boolean useSymmetry = tdPar.useSymmetry;
		// Is needed, because every player has his own LUT-set
		int nextPlayer = (curPlayer == ConnectFour.PLAYER1 ? ConnectFour.PLAYER2
				: ConnectFour.PLAYER1);

		y = getValue(curPlayer, curZobr, curBoard);

		// the reward is in range [-1,1]. We map it to range [0,1] for
		// the non-linear IDBD version and AUTOSTEP to adjust it to the range of
		// the logistic sigmoid:
		if (finished)
			reward = ActivationFunction.scaleReward(reward, tdPar.activation);

		// Target-Signal
		tg = (finished ? reward : gamma
				* getValue(nextPlayer, nextZobr, nextBoard));

		double delta = tg - y;

		// Get equivalent Board
		long[] equiv = null;
		if (useSymmetry)
			equiv = mirror(curBoard);
		int i[] = getIndexSet(curZobr, curBoard, equiv);

		// Get index for player
		int p = (curPlayer == ConnectFour.PLAYER1 ? 0 : 1);

		// Get the derivative of y = V(s_t)
		double deriv_i = ActivationFunction.getDeriv(y, tdPar.activation);

		// Some methods require additional calculations and parameter-adaptions
		// (e.g., adjusting the learning rates) before the actual weight-update
		// is performed.
		UpdateMethod upMeth = tdPar.updateMethod;
		if (upMeth == UpdateMethod.K1 || upMeth == UpdateMethod.ELK1) {
			// K1-Method: Update Beta first and sum up all affected alpha_i
			// calculate M = sum_i{alpha_i(t+1)*(x_i)^2}
			alpha = updateAndGetGlobalAlphaELK1(delta, useSymmetry, deriv_i, i,
					p);
		} else if (upMeth == UpdateMethod.AUTOSTEP) {
			alpha = getGlobalAlphaAUTOSTEP(i, delta, useSymmetry, p);
		} else if (upMeth == UpdateMethod.ALPHABOUND) {
			int[] i_1 = null;
			if (tdPar.lambda != 0.0) {
				equiv = null;
				if (useSymmetry)
					equiv = mirror(nextBoard);
				i_1 = getIndexSet(nextZobr, nextBoard, equiv);
			}
			alpha = getGlobalAlphaBound(i, i_1, p, deriv_i);
		}

		if (updateMeth != UpdateMethod.TCL || !useEpisodes) {
			if (tdPar.lambda == 0.0) // Simple update without TCL-episodes
				update(curPlayer, delta, y, false, i);
			else {
				UpdateParams u;
				double derivY = ActivationFunction.getDeriv(y, tdPar.activation);
				// Here only delta, derivY and y are needed to bet set
				// i and e_i are set later, so set to arbitrary values here
				u = new UpdateParams(-999, alpha, delta, derivY, -999, y);
				updateWithEligTraces(u);
			}
		} else
			updateEpisodicTCL(curPlayer, curZobr, curBoard, delta, y);
		
		// TODO: Added 14.07.2014
		if(weights[0][0].lr.doPostUpdateTask()) {
			int numTuples = weights[0].length;
			for (int k = 0; k < numTuples; k++) {
				weights[p][k].lr.postUpdateTask(finished);
			}
		}
	}

	/**
	 * @param curPlayer
	 *            current player. Needed for the divided LUT
	 * @param i
	 * @param i_1
	 * @param dW
	 *            value, that is added to the weights
	 */
	private void update(int curPlayer, double delta, double y,
			boolean accumulate, int[] i) {
		int k;
		boolean useSymmetry = tdPar.useSymmetry;
		int numTuples = weights[0].length;

		// Get equivalent Board
		// if (useSymmetry)
		// equiv = mirror(board);

		// int i[] = getIndexSet(zobr, board, equiv);

		// Get the derivative of y = V(s_t)
		double deriv_i = ActivationFunction.getDeriv(y, tdPar.activation);

		// Because here lambda == 0.0, the eligibility trace vector is
		// simply the Gradient of the Loss-function to the weights.
		// For a MSE-loss the gradient includes a factor of tanh'=(1-y^2) etc.
		// Using Anna Koops Loss function, the gradient is simply the input
		// vector \vec(x).
		double elig_i = ActivationFunction.weightLossDeriv(y, tdPar.activation);

		// Get index for player
		int p = (curPlayer == ConnectFour.PLAYER1 ? 0 : 1);

		// ##############################################################
		UpdateParams u_i = new UpdateParams(-1, alpha, delta, deriv_i, 1, y);
		if (lrCommon != null)
			lrCommon.commonPreUpdateTask(u_i);
		for (k = 0; k < numTuples; k++) {
			u_i.e_i = elig_i;
			if (i[k] == i[k + numTuples] && !USEBINARYFV)
				u_i.e_i = 2 * elig_i;
			u_i.i = i[k];
			weights[p][k].update(u_i);
		}
		for (int j = 0; useSymmetry && j < numTuples; k++, j++) {
			if (i[k] == i[k - numTuples])
				continue;
			u_i.e_i = elig_i;
			u_i.i = i[k];
			weights[p][j].update(u_i);
		}
		if (lrCommon != null)
			lrCommon.commonPostUpdateTask(u_i);
	}

	private double getEligSum(int p, int[] i, double deriv_y) {
		int k;
		int numTuples = tdPar.tupleNum;
		boolean useSymmetry = tdPar.useSymmetry;
		int x_i;
		double sum = 0.0;
		for (k = 0; k < numTuples; k++) {
			x_i = 1;
			if (i[k] == i[k + numTuples] && !USEBINARYFV)
				x_i = 2;
			if (tdPar.lambda != 0.0)
				sum += x_i * weights[p][k].getElig(i[k]);
			else
				// e_t(i) = x_i * (1-y^2)
				sum += x_i * deriv_y * x_i;
		}
		for (int j = 0; useSymmetry && j < numTuples; k++, j++) {
			if (i[k] == i[k - numTuples])
				continue;
			if (tdPar.lambda != 0.0)
				sum += weights[p][j].getElig(i[k]);
			else
				sum += 1.0 * deriv_y;
		}
		return sum;
	}

	private double getGlobalAlphaBound(int[] i, int[] i_1, int p, double deriv_y) {
		int nextP = 1 - p; // Next player is 0 or 1
		// Get x_t * e_t
		double x_tDOTe_t = getEligSum(p, i, deriv_y);
		double x_t1DOTe_t = 0.0;

		// Only makes sense to calculate this, if lambda != 0
		if (tdPar.lambda != 0.0)
			x_t1DOTe_t = tdPar.gamma * getEligSum(nextP, i_1, deriv_y);

		double den = Math.abs(deriv_y * (x_t1DOTe_t - x_tDOTe_t));
		return Math.min(alpha, 1.0 / den);
	}

	private double updateAndGetGlobalAlphaELK1(double delta,
			boolean useSymmetry, double deriv, int[] i, int p) {
		 //TODO: For eligibility traces
		int k;
		int numTuples = weights[0].length;
		double M = 0.0;
		double x_i;
		for (k = 0; k < numTuples; k++) {
			x_i = 1;
			if (i[k] == i[k + numTuples] && !USEBINARYFV)
				x_i = 2;
			ELK1 elk1 = (ELK1) weights[p][k].lr;
			elk1.adjustBeta(i[k], delta, deriv, x_i);
			M += elk1.getAlpha(i[k]) * x_i * x_i;
		}
		for (int j = 0; useSymmetry && j < numTuples; k++, j++) {
			if (i[k] == i[k - numTuples])
				continue; // was done in previous loop
			ELK1 elk1 = (ELK1) weights[p][j].lr;
			elk1.adjustBeta(i[k], delta, deriv, 1.0);
			M += elk1.getAlpha(i[k]); // (x_i)^2 = 1
		}
		return 1.0 / ((1.0 + M) * deriv * deriv);
	}

	private double getGlobalAlphaAUTOSTEP(int[] i, double delta,
			boolean useSymmetry, int p) {
		double M = 0.0;
		double x_i;
		int numTuples = weights[0].length;
		int k;
		 //TODO: For eligibility traces
		for (k = 0; k < numTuples; k++) {
			x_i = 1;
			if (i[k] == i[k + numTuples] && !USEBINARYFV)
				x_i = 2;
			AutoStep as = (AutoStep) weights[p][k].lr;
			M += as.auto_update_v_alpha(i[k], delta, x_i) * x_i * x_i;
		}
		for (int j = 0; useSymmetry && j < numTuples; k++, j++) {
			if (i[k] == i[k - numTuples])
				continue; // was done in previous loop
			AutoStep as = (AutoStep) weights[p][j].lr;
			M += as.auto_update_v_alpha(i[k], delta, 1.0);
		}
		M = Math.max(M, 1);
		return 1.0 / M;
	}

	private void updateEpisodicTCL(int curPlayer, long curZobr,
			long[] curBoard, double delta, double y) {
		episodeCounter++;
		// TODO: For elig-traces...............
		double elig =ActivationFunction.weightLossDeriv(y, tdPar.activation);

		// Accumulate recommended weight change
		tclAccumulateRWC(curPlayer, curBoard, curZobr, delta, elig);

		if (episodeCounter == tdPar.tclEpisodeLength) {
			episodeCounter = 0;
			tclEpsisodeUpdate(alpha);
		}
	}

	private void tclAccumulateRWC(int curPlayer, long[] board, long zobr,
			double delta, double elig) {
		int i;
		long[] equiv = null;

		boolean useSymmetry = tdPar.useSymmetry;
		int numTuples = weights[0].length;

		// Get equivalent Board
		if (useSymmetry)
			equiv = mirror(board);

		int indexSet[] = getIndexSet(zobr, board, equiv);

		// Get index for player
		int p = (curPlayer == ConnectFour.PLAYER1 ? 0 : 1);
		// Accumulate: Only for TCL with Update-Episodes
		double rwc = delta * elig;
		for (i = 0; i < numTuples; i++) {
			TemporalCoherence tc = (TemporalCoherence) weights[p][i].lr;
			tc.accuRecWeightChange(indexSet[i], rwc);
		}
		for (int j = 0; useSymmetry && j < numTuples; i++, j++) {
			TemporalCoherence tc = (TemporalCoherence) weights[p][j].lr;
			tc.accuRecWeightChange(indexSet[i], rwc);
		}
	}

	private void tclEpsisodeUpdate(double alpha) {
		int numTuples = weights[0].length;
		TemporalCoherence tc;
		for (int i = 0; i < numTuples; i++) {
			tc = (TemporalCoherence) weights[0][i].lr;
			tc.tclEpsisodeUpdate(alpha);
			tc = (TemporalCoherence) weights[1][i].lr;
			tc.tclEpsisodeUpdate(alpha);
		}
	}

	private void updateWithEligTraces(UpdateParams u) {
		// Update weights
		for (int i = 0; i < weights[0].length; i++) {
			weights[0][i].updateLUTwithElig(u);
			weights[1][i].updateLUTwithElig(u);
		}
	}

	public void resetElig(long board[], long zobr, int playerToMove) {
		int i;
		int numTuples = weights[0].length;
		// reset Elig-Vector
		for (i = 0; i < numTuples; i++) {
			weights[0][i].resetElig();
			weights[1][i].resetElig();
		}

		// Init elig with the gradient for the given board (normally empty
		// board) grad_w{ f(w,g(s_0)) }
		updateElig(board, zobr, playerToMove);
	}

	public void updateElig(long board[], long zobr, int playerToMove) {

		double v = getValue(playerToMove, zobr, board);
		// double grad = (1 - v * v); // grad=feature-vector*(1-v0^2)
		double grad = ActivationFunction.weightLossDeriv(v, tdPar.activation);
		// board contains the state s_{t+1}
		// playerToMove: The player to move for the given board.

		// Gradient is dependent on player whos turn it is on the board
		// All elements of the inactive player are set to zero,
		// therefore only update elig-elements of curPlayer
		boolean useSymmetry = tdPar.useSymmetry;
		long[] equiv = null;
		if (useSymmetry)
			equiv = mirror(board);

		int i[] = getIndexSet(zobr, board, equiv);
		int numTuples = weights[0].length;
		int index = (playerToMove == ConnectFour.PLAYER1 ? 0 : 1);

		// First scale the current elig-vector by lambda*gamma
		// e <- e * lambda * gamma
		// The scaling has to be done for all LUTs (for both players). All
		// elements <> 0 in the elig-vector are therefore scaled.
		scaleElig();

		// The feature-vector g(s_{t+1}) sets all elements for the other player
		// to zero. Also the gradient-elements will be zero for those. We only
		// have to add the gradient to the other half of g(s).
		// Those weights, for which the feature-vector is <> 0 need to add the
		// gradient. For all other weights the gradient is zero.
		// e <- e + grad_w(f(w, g(s)))
		int k;
		double eAdd_i = 0.0;
		for (k = 0; k < numTuples; k++) {
			eAdd_i = grad;
			if (i[k] == i[k + numTuples] && !USEBINARYFV)
				eAdd_i *= 2;
			weights[index][k].addGradToElig(i[k], eAdd_i);
		}
		for (int j = 0; useSymmetry && j < numTuples; k++, j++) {
			if (i[k] == i[k - numTuples])
				continue;
			weights[index][j].addGradToElig(i[k], grad);
		}
	}

	public void scaleElig() {
		double scale = tdPar.gamma * tdPar.lambda;
		int i;
		int numTuples = weights[0].length;
		for (i = 0; i < numTuples; i++) {
			weights[0][i].scaleElig(scale);
			weights[1][i].scaleElig(scale);
		}
	}

	public int countActiveEligTraces() {
		int num = 0;
		int numTuples = weights[0].length;
		for (int i = 0; i < numTuples; i++) {
			num += weights[0][i].countActiveEligTraces();
			num += weights[1][i].countActiveEligTraces();
		}
		return num;
	}

	public double getLinearValue(int curPlayer, long zobr, long[] board) {
		int i;
		double score = 0.0;
		long[] equiv = null;

		int numTuples = weights[0].length;
		boolean useSymmetry = tdPar.useSymmetry;

		// Get equivalent board
		if (useSymmetry)
			equiv = mirror(board);

		// Get index for player
		int index = (curPlayer == ConnectFour.PLAYER1 ? 0 : 1);

		int indexSet[] = getIndexSet(zobr, board, equiv);

		// Here the input x_i is not needed, because it is a completly linear
		// operation.
		for (i = 0; i < numTuples; i++)
			score += weights[index][i].getWeight(indexSet[i]);
		if (useSymmetry)
			for (int j = 0; j < numTuples; i++, j++)
				if (indexSet[i] != indexSet[i - numTuples] || !USEBINARYFV)
					score += weights[index][j].getWeight(indexSet[i]);
		return score;
	}

	/**
	 * Get the Value for a given board
	 * 
	 * @param curPlayer
	 *            current player. Needed for the divided LUT
	 * @param zobr
	 *            Zobrist Key of the board. For zobr=-1 the Index-Set to the
	 *            board will be calculated and not retreived from the
	 *            hash-table.
	 * @param board
	 *            bit-boards of both players
	 * @return Value for this board
	 */
	public double getValue(int curPlayer, long zobr, long[] board) {
		Activation activation = tdPar.activation;
		double linearVal = getLinearValue(curPlayer, zobr, board);
		double sigFac = tdPar.sigOutputFac;

		return ActivationFunction.activation(linearVal * sigFac, activation);
		
//		switch(activation) {
//		case LOGSIG:
//			return 1.0 / (1.0 + Math.exp(-linearVal * sigFac));
//		case TANH:
//			return MY_TANH.tanh(linearVal * sigFac);
//		case NONE:
//		default:
//			return linearVal;
//		}
	}

	/**
	 * Get all needed indexes for the given board. If possible (and wanted), the
	 * keys will be retreived from a hash-table
	 * 
	 * @param zobr
	 *            Zobrist Key of the board. For zobr=-1 the Index-Set to the
	 *            board will be calculated and not retreived from the
	 *            hash-table.
	 * @param board
	 *            bit-boards of both players (position)
	 * @param equiv
	 *            bit-boards of both players (symmetric position)
	 * @return A List of all indexes for the position. If symmetries are used,
	 *         the second half of the list contains the indexes for the
	 *         symmetric board.
	 */
	private int[] getIndexSet(long zobr, long board[], long equiv[]) {
		if (zobr != -1L) {
			int index = ((int) zobr & (transPosSize - 1));
			if (trKey[index] == zobr && trIndexList[index] != null)
				return trIndexList[index];

			// Wasn't Found, generate new
			int[] indexSet = generateIndexSet(board, equiv);

			// Write to Hash-Table
			trKey[index] = zobr;
			trIndexList[index] = indexSet;
			return indexSet;
		}
		return generateIndexSet(board, equiv);
	}

	/**
	 * Calculates all indexes to a given board, if a hash-miss has occured or no
	 * hash-table is used.
	 * 
	 * @param board
	 *            bit-boards of both players (position)
	 * @param equiv
	 *            bit-boards of both players (symmetric position)
	 * @return A List of all indexes for the position. If symmetries are used,
	 *         the second half of the list contains the indexes for the
	 *         symmetric board.
	 */
	private int[] generateIndexSet(long board[], long equiv[]) {
		int numTuples = weights[0].length;

		int[] indexSet = new int[numTuples * (equiv == null ? 1 : 2)];
		int i, index;
		int cellVals[][] = getCellValues(board[0], board[1]);

		for (i = 0; i < numTuples; i++) {
			index = weights[0][i].getFeature(cellVals, false);
			indexSet[i] = index;
		}

		if (equiv != null)
			for (int j = 0; j < numTuples; i++, j++) {
				index = weights[0][j].getFeature(cellVals, true);
				indexSet[i] = index;
			}
		return indexSet;
	}

	public int[][] getCellValues(long fieldP1, long fieldP2) {
		int[][] cellVals = new int[NUMCOLS][NUMROWS];
		long mask;
		int posVals = tdPar.posVals;
		for (int i = 0; i < NUMCOLS; i++) {
			for (int j = 0; j < NUMROWS; j++) {
				mask = ConnectFour.fieldMask[i][j];

				if ((fieldP1 & mask) != 0L)
					cellVals[i][j] = 1; // 1 -> for player 1
				else if ((fieldP2 & mask) != 0L)
					cellVals[i][j] = 2; // 2 -> for player 2

				// Cell must be empty
				else if (posVals == 4) { // Check if reachable
					if (j == 0)
						cellVals[i][j] = 3;
					else if (cellVals[i][j - 1] == 1 || cellVals[i][j - 1] == 2)
						// row is > 0
						cellVals[i][j] = 3;
				}
			}
		}

		return cellVals;
	}

	/**
	 * Count (active) weights for the n-tuple set
	 * 
	 * @param verbose
	 *            0: print nothing, 1: .. one line, 2: .. several lines
	 * @return a vector with three counts (sum over all n-tuples and their
	 *         LUTs): [0] weights, [1] realizable states, [2] non-zero weights
	 * 
	 * @author Wolfgang Konen /WK/
	 */
	public long[] countActiveWeights(int verbose) {
		int i, p;
		long[] nWeights = new long[3]; // 0: weights, 1: realizable states, 2:
										// non-zero weights

		int numTuples = tdPar.tupleNum;
		for (p = 0; p < 2; p++) {
			for (i = 0; i < numTuples; i++) {
				nWeights[0] += (long) Math.pow(nTuples[p][i].getPosVals(),
						nTuples[p][i].getLength());
				nWeights[1] += nTuples[p][i].countRealizableStates();
				nWeights[2] += nTuples[p][i].countNonZeroLUTWeights();
				i = i + 1;
				i = i - 1;
			}
		}
		DecimalFormat df = new DecimalFormat("##0.0");
		DecimalFormat df2 = new DecimalFormat("0,000,000");
		if (verbose == 2) {
			System.out.println("Number of weights: " + df2.format(nWeights[0]));
			System.out.println("Realizable states: " + df2.format(nWeights[1]));
			System.out.println("Non-zero  weights: " + df2.format(nWeights[2])
					+ "  ("
					+ df.format((double) nWeights[2] / nWeights[1] * 100)
					+ "% of realizable states)");
		}
		if (verbose == 1) {
			System.out.println("Number of weights; states; non-0: "
					+ df2.format(nWeights[0]) + "; " + df2.format(nWeights[1])
					+ "; " + df2.format(nWeights[2]) + "  ("
					+ df.format((double) nWeights[2] / nWeights[1] * 100)
					+ "%)");
		}

		return nWeights;
	}

	/**
	 * Set the Size of the Hash-Table
	 * 
	 * @param hashSize
	 */
	public void initHash(int hashSize) {
		if (this.transPosSize != hashSize || trKey == null
				|| trIndexList == null) {
			this.transPosSize = hashSize;
			trIndexList = new int[transPosSize][];
			trKey = new long[transPosSize];
		} else if (hashSize == 0) {
			trIndexList = null;
			trKey = null;
		}
		System.gc();
	}

	/**
	 * @return Get the value for the learning-rate alpha
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * @return Get the value for trace decay lambda
	 */
	public double getLambda() {
		return tdPar.lambda;
	}

	/**
	 * @return alpha-change-ratio
	 */
	public double getAlphaChangeRatio() {
		return alphaChangeRatio;
	}

	/**
	 * @return Number of used n-Tuples
	 */
	public int getNumTuples() {
		return tdPar.tupleNum;
	}

	/**
	 * @return All N-Tuple Objects. Each N-Tuple is created for both players.
	 *         These are always directly behind each other.
	 */
	public NTupleC4[] getNTuples() {
		int numTuples = tdPar.tupleNum;
		NTupleC4 list[] = new NTupleC4[numTuples * 2];
		for (int j = 0, k = 0; j < nTuples[0].length; j++)
			for (int i = 0; i < nTuples.length; i++)
				list[k++] = nTuples[i][j];
		return list;
	}

	/**
	 * @param dim
	 *            Dimension for output
	 * @return All n-Tuple-Sets as a List of Strings
	 */
	public String[] getNTuples(int dim) {
		int numTuples = tdPar.tupleNum;
		String str[] = new String[numTuples];
		for (int i = 0; i < numTuples; i++)
			str[i] = nTuples[0][i].toString(dim);
		return str;
	}

	/**
	 * Get a List of all N-Tuples in an Integer-Field
	 * 
	 * @return List of n-Tuples (2-dim)
	 */
	public Integer[][][] getNTuples2Dim() {
		int numTuples = tdPar.tupleNum;
		Integer tup[][][] = new Integer[numTuples][][];
		for (int i = 0; i < numTuples; i++) {
			tup[i] = nTuples[0][i].getNTuple2dim();
		}
		return tup;
	}

	/**
	 * Get a List of all N-Tuples in an Integer-Field
	 * 
	 * @return List of n-Tuples (1-dim)
	 */
	public Integer[][] getNTuples1Dim() {
		int numTuples = tdPar.tupleNum;
		Integer tup[][] = new Integer[numTuples][];
		for (int i = 0; i < numTuples; i++) {
			tup[i] = nTuples[0][i].getNTuple1dim();
		}
		return tup;
	}

	/**
	 * @param dim
	 *            Dimension (1,2)
	 * @return One single String containing all n-Tuples
	 */
	public String toString(int dim) {
		int numTuples = tdPar.tupleNum;
		String str = new String();
		for (int i = 0; i < numTuples; i++)
			str += nTuples[0][i].toString(dim) + "\n";
		return str;
	}

	/**
	 * Get the Size of ALL lookup-tables (LUT)
	 * 
	 * @param inBytes
	 *            true, if size shall be returned in Bytes
	 * @return Number of Elements in all LUTs or the Size in Bytes
	 */
	public int getLUTSize(boolean inBytes) {
		int numTuples = tdPar.tupleNum;
		int size = 0;
		for (int i = 0; i < numTuples; i++)
			// mult. with 2, because N-Tuple-System for each player
			size += nTuples[0][i].getLUTSize(inBytes) * 2;
		return size;
	}

	public String getCommonLR() {
		if (lrCommon != null)
			return lrCommon.toString();
		return new String();
	}
}

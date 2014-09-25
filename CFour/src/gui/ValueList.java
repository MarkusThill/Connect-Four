package gui;

import nTupleTD.NTupleC4;
import c4.ConnectFour;

/**
 * Allows to create all the data for ShowValueList
 * 
 * @author Markus Thill
 * 
 */
public class ValueList {
	public int indexes[][];
	public double[][][] values;
	double sumValues[][];
	double score[];
	double scoreTanh[];

	// private values
	private int nTupleListLength;
	private static final int NUMPLAYERS = 2;
	private static final int PLAYERX = 0;
	private static final int PLAYERO = 1;

	private static final int MIRRORCOUNT = 2;
	private static final int NOTMIRRORED = 0;
	private static final int MIRRORED = 1;

	public ValueList(NTupleC4[] nTupleList, int board[][]) {
		if (nTupleList != null) {
			nTupleListLength = nTupleList.length;
			initData();
			calcValues(nTupleList, board);
			calSum();
		}
	}

	private void initData() {

		int tupNum = nTupleListLength / 2;

		// Init data
		values = new double[NUMPLAYERS][tupNum][MIRRORCOUNT];
		indexes = new int[tupNum][MIRRORCOUNT];
		sumValues = new double[NUMPLAYERS][MIRRORCOUNT];
		score = new double[NUMPLAYERS];
		scoreTanh = new double[NUMPLAYERS];
	}

	private void calcValues(NTupleC4[] nTupleList, int board[][]) {
		for (int j = 0; j < nTupleListLength; j += 2) {
			NTupleC4 nt0 = nTupleList[j];
			NTupleC4 nt1 = nTupleList[j + 1];
			int[][] mirroredBoard = ConnectFour.getMirroredField(board);
			int index0 = nt0.getFeature(board);
			int index1 = nt1.getFeature(mirroredBoard);

			values[PLAYERX][j / 2][NOTMIRRORED] = nt0.getWeight(index0);
			values[PLAYERX][j / 2][MIRRORED] = nt0.getWeight(index1);
			values[PLAYERO][j / 2][NOTMIRRORED] = nt1.getWeight(index0);
			values[PLAYERO][j / 2][MIRRORED] = nt1.getWeight(index1);
			indexes[j / 2][NOTMIRRORED] = index0;
			indexes[j / 2][MIRRORED] = index1;
		}
	}

	private void calSum() {
		// Sum of the Values
		for (int i = 0; i < values[0].length; i++) {
			for (int j = 0; j < NUMPLAYERS; j++) {
				sumValues[j][NOTMIRRORED] += values[j][i][NOTMIRRORED];
				sumValues[j][MIRRORED] += values[j][i][MIRRORED];
			}
		}

		score[PLAYERX] = sumValues[PLAYERX][NOTMIRRORED]
				+ sumValues[PLAYERX][MIRRORED];
		score[PLAYERO] = sumValues[PLAYERO][NOTMIRRORED]
				+ sumValues[PLAYERO][MIRRORED];

		scoreTanh[PLAYERX] = Math.tanh(score[PLAYERX]);
		scoreTanh[PLAYERO] = Math.tanh(score[PLAYERO]);
	}
}

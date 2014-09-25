package nTuple;

import java.util.Random;

public class NTupleBase {
	protected int length;
	protected double lut[];
	protected int posVals;
	protected Random rnd = new Random();
	
	
	public NTupleBase(int length, int posVals) {
		setParams(length, posVals);
	}
	
	public void setParams(int length, int posVals) {
		int lutSize;
		this.length = length;
		this.posVals = posVals;
		lutSize = (int)Math.pow(posVals, length);
		lut = new double[lutSize];
	}
	
	public double getScore(int index) {
		return lut[index];
	}
	
	public void initWeights(double val) {
		for(int i=0;i<lut.length;i++)
			lut[i] = val;
	}
}

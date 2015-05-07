package adaptableLearningRates;

import nTupleTD.TDParams;
import nTupleTD.UpdateParams;

public class SMD extends LearningRates{
	private static final long serialVersionUID = 8596040183066185210L;
	private float alpha[];
	private float h[];
	
	public SMD(TDParams tdPar, int length) {
		super(null, tdPar);
		createIDBDTables(length);
	}

	private void createIDBDTables(int length) {
		alpha = new float[length];
		h = new float[length];
		for (int i = 0; i < length; i++) {
			alpha[i] = (float) tdPar.alphaInit;
		}
		
	}

	@Override
	public void preWeightUpdateTask(UpdateParams u_i) {
		int i = u_i.i;
		double theta = tdPar.idbdTheta;
		double b = 1.0 + theta * u_i.delta * u_i.e_i * h[i];
		alpha[i] = (float) (alpha[i] * Math.max(0.5, b));
	}

	@Override
	public double getLearningRate(UpdateParams u_i) {
		return alpha[u_i.i];
	}

	@Override
	public void postWeightUpdateTask(UpdateParams u_i) {
		int i = u_i.i;
		double alpha = getLearningRate(u_i);
		double mu = 1.0; //TODO: Where to get mu from???
		double H = u_i.globalAlpha; // is hidden in global alpha
		h[i] = (float) (mu * h[i] - mu * alpha * u_i.derivY * u_i.e_i * H + alpha * u_i.delta * u_i.e_i);
	}

	@Override
	public TDParams getBestParams(TDParams tdPar) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public double getH(int i, double x_i) {
		return x_i * h[i];
	}
}

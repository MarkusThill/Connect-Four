package adaptableLearningRates;

import java.io.Serializable;

import nTupleTD.TDParams;
import nTupleTD.UpdateParams;


/**
 * An abstract class, mainly providing abstract methods for classes implementing learning-rate systems.  
 * @author Markus Thill
 *
 */
public abstract class LearningRates implements Serializable {
	private static final long serialVersionUID = 8076732099488780681L;
	protected final TDParams tdPar;
	protected boolean doPostUpdateTask  = false;
	
	public interface LRCommon {
		public void commonPreUpdateTask(UpdateParams u_i);

		public double commonGetLearningRate(UpdateParams u_i);

		public void commonPostUpdateTask(UpdateParams u_i);
		
	}
	
	protected final LRCommon com;
	
	LearningRates(LRCommon com, TDParams tdPar) {
		this.com = com;
		this.tdPar = tdPar;
	}
	
	

	/**
	 * General calculation of the weight change for a single weight w_i.
	 * 
	 * @param u_i
	 * @return
	 */
	public double getWeightChange(UpdateParams u_i) {
		double stepSize = getLearningRate(u_i);
		double deltaW = stepSize * u_i.delta * u_i.e_i;
		return deltaW;
	}
	
	public boolean doPostUpdateTask() {
		return doPostUpdateTask;
	}
	

	public abstract void preWeightUpdateTask(UpdateParams u_i);

	public abstract double getLearningRate(UpdateParams u_i);

	public abstract void postWeightUpdateTask(UpdateParams u_i); // Called after each indivdual weight is updated
	
	public void postUpdateTask(boolean gameFinished){} // Called after all weights of corresponding Weight-Subset were updated  
	
	public abstract TDParams getBestParams(final TDParams tdPar);
		
}

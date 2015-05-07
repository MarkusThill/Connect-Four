package nTupleTD;

public class UpdateParams {
	public int i; // cannot be final
	public final double globalAlpha;
	public final double delta;
	//public final double gradLossW;
	public final double derivY;
	public double e_i; // cannot be final
	public double x_i;
	public final double y;
	public double w_i;

	public UpdateParams(int i, double globalAlpha, double delta, double derivY,
			double e_i, double x_i, double y) {
		this.i = i;
		this.globalAlpha = globalAlpha;
		this.delta = delta;
		//this.gradLossW = gradLossW;
		this.derivY = derivY;
		this.e_i = e_i;
		this.x_i = x_i;
		this.y = y;
	}
}
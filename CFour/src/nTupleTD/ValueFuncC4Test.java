package nTupleTD;

import junit.framework.TestCase;

public class ValueFuncC4Test extends TestCase{
	private ValueFuncC4 vf;
	
	public void testGenerateRandomPoints() {
		boolean print = false;
		Integer[] x = NTupleFactory.generateRandomPoints(10, false);
		assertEquals(10, x.length);
		
		x = NTupleFactory.generateRandomPoints(5, false);
		assertEquals(5, x.length);
		
		// int tupleLen, int tupleNum, int mode, int posVals, boolean useSymmetry, boolean randInitWeights
		TDParams tdPar = new TDParams();
		tdPar.randTupleLen = 5;
		tdPar.tupleNum = 5;
		tdPar.randMode = 0;
		tdPar.posVals = 3;
		tdPar.useSymmetry = true;
		tdPar.randInitWeights = false;
		tdPar.randNTuples = true;
		
		vf = new ValueFuncC4(tdPar);
		if(print) {
			System.out.println("5x5-Random Points");
			System.out.print(vf.toString(1));
			System.out.print(vf.toString(2));
		}
		
		tdPar.randTupleLen = 3;
		tdPar.tupleNum = 5;
		tdPar.randMode = 0;
		vf = new ValueFuncC4(tdPar);
		if(print) {
			System.out.println("\n5x3-Random Points");
			System.out.print(vf.toString(1));
			System.out.print(vf.toString(2));
		}
		
		tdPar.randTupleLen = 10;
		tdPar.tupleNum = 3;
		tdPar.randMode = 1;
		vf = new ValueFuncC4(tdPar);
		if(print) {
			System.out.println("\n3x(10)-Random Points");
			System.out.print(vf.toString(1));
			System.out.print(vf.toString(2));
		}
	}
	
	public void testGenerateRandomWalk() {
		TDParams tdPar = new TDParams();
		tdPar.randTupleLen = 5;
		tdPar.tupleNum = 5;
		tdPar.randMode = 2;
		tdPar.posVals = 3;
		tdPar.useSymmetry = true;
		tdPar.randInitWeights = false;
		tdPar.randNTuples = true;
		
		boolean print = false;
		vf = new ValueFuncC4(tdPar);
		if(print) {
			System.out.println("\n5x5-Random Walk");
			System.out.print(vf.toString(2));
		}
		assertEquals(5, vf.getNumTuples());
		
		tdPar.randTupleLen = 10;
		tdPar.tupleNum = 3;
		tdPar.randMode = 3;
		vf = new ValueFuncC4(tdPar);
		if(print) {
			System.out.println("\n3x(5)-Random Walk");
			System.out.print(vf.toString(2));
		}
	}
}

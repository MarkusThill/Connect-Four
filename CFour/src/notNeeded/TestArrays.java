package miscellaneous;

import org.omg.CORBA.LongSeqHelper;

public class TestArrays {

	public static void castObj(Object[] obj) {
		Object p;
		System.out.println(obj[0].getClass());
		for(int i=0;i<obj.length;i++)
			p = obj[i];
	}
	
	public static void testOp(long x) {
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[][] x = new int[20][10];
		long[][] y = new long[20][10];
		Long z[] = new Long[20];
		for(int i=0, k=0;i<20;i++)
			for(int j=0;j<10;j++)
				x[i][j] = k++;
		
		castObj(x);
		Class classX = x.getClass();
		Class classY = y.getClass();
		if(classX == int[][].class)
			System.out.println(classX);
		if(classY == long[][].class)
			System.out.println(y.getClass());
		
		castObj(z);
	}

}

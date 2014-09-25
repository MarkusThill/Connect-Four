package miscellaneous;

public class Tools {
	// only for numbers 2^n
		/**
		 * Fast Algorithm to calculate the position of a set Bit in a long-variable.
		 * Algorithm-complexity for variables with the length n: O(log(n))
		 * 
		 * @param x
		 *            Number in the form 2^n
		 * @return binary logarithm (Position of the set Bit in a long-variable)
		 */
		public static int ld(long x) {
			int ld = 0;
			long mask = 0xFFFFFFFFL;
			int n = 32;

			while (n != 0) {
				if ((x & mask) == 0l) {
					ld += n;
					x >>= n;
				}
				n >>= 1;
				mask >>= n;
			}
			return ld;
		}
		
		public static long[] toPrimitiveLong(Long[] array) {
			long[] primitiveLong = null;
			if(array != null) {
			primitiveLong = new long[array.length];
			for(int i=0;i<array.length;i++)
				primitiveLong[i] = array[i];
			}
			return primitiveLong;
		}
		
		public static int[] toPrimitiveInt(Integer[] array) {
			int[] primitiveInt = null;
			if(array != null) {
			primitiveInt = new int[array.length];
			for(int i=0;i<array.length;i++)
				primitiveInt[i] = array[i];
			}
			return primitiveInt;
		}
		
		public static int[][] toPrimitiveInt(Integer[][] array) {
			int[][] primitiveInt = null;
			if(array != null) {
			primitiveInt = new int[array.length][array[0].length];
			for(int i=0;i<array.length;i++)
				for(int j=0;j<array[i].length;j++)
				primitiveInt[i][j] = array[i][j];
			}
			return primitiveInt;
		}
}

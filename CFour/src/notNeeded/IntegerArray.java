package miscellaneous;

public class IntegerArray {

	private static final int ARRSIZE = 10000000;
	private int array[];
	
	void fill() {
		
		array = new int[ARRSIZE];
		for(int i=0;i<ARRSIZE;i++)
			array[i] = i;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IntegerArray myArray = new IntegerArray();
		System.out.println("Start");
		myArray.fill();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Stop");

	}

}

package miscellaneous;

import java.util.Random;

//import java.util.Random;

public class MyTanh {

	private static final int NUMPOINTS = 1000;
	private static final float MAXVALUE = 12;
	private static final float INTERVALL = MAXVALUE / NUMPOINTS;
	private float sVal[] = new float[NUMPOINTS + 1];

	public MyTanh() {
		for (int i = 0; i < NUMPOINTS; i++) {
			sVal[i] = (float) Math.tanh(i * INTERVALL);
		}
		sVal[NUMPOINTS] = (float) Math.tanh(MAXVALUE);
	}

	public float tanh(double x) {
		float val = (float) x;
		int sgn = 1;
		if (val < 0.0) {
			sgn = -1;
			val *= sgn;
		}

		if (val >= MAXVALUE) // Grenzwert
			return sgn;

		int entryNum = (int) ((val / MAXVALUE) * NUMPOINTS);
		float iVal = val - entryNum * INTERVALL;
		float m = (sVal[entryNum + 1] - sVal[entryNum]) / INTERVALL;
		float result = sVal[entryNum] + m * iVal;
		return sgn * result;
	}

	public void test() {
		float res = 0;
		Random rand = new Random();

		long runs = 99999999;

		long zstStart = System.currentTimeMillis();
		for (long i = 0; i < runs; i++) {
			double testVal = rand.nextDouble();
			float x1 = tanh(testVal);
			res = x1;
		}
		System.out.print(res + "\n");
		long zstStop = System.currentTimeMillis();
		long time = zstStop - zstStart;
		System.out.println("Time needed (myTanh):" + time);

		zstStart = System.currentTimeMillis();
		for (long i = 0; i < runs; i++) {
			double testVal = rand.nextDouble();
			float x2 = (float) Math.tanh(testVal);
			res = x2;
		}
		System.out.print(res + "\n");
		zstStop = System.currentTimeMillis();
		time = zstStop - zstStart;
		System.out.println("Time needed (Math.tanh):" + time);
	}

	public void test2() {
		float res = 0;
		Random rand = new Random();

		long runs = 99999999;

		long zstStart = System.currentTimeMillis();
		for (long i = 0; i < runs; i++) {
			double testVal = rand.nextDouble() * (rand.nextInt(20) - 10);
			float x1 = tanh(testVal);
			float x2 = (float) Math.tanh(testVal);
			res = Math.abs(x1 - x2);
			if (res > 0.000001)
				System.out.println(x1 + "\n" + x2);
		}
		long zstStop = System.currentTimeMillis();
		long time = zstStop - zstStart;
		System.out.println("Time needed (Test2):" + time);
	}

	public static void main(String[] args) {
		MyTanh t = new MyTanh();

		// System.out.println(t.myTanh(-9.9));
		// System.out.println(Math.tanh(-9.9));
		t.test();
		//t.test2();
	}
}
